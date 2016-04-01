/*******************************************************************************
 *     SDR Trunk 
 *     Copyright (C) 2014-2016 Dennis Sheirer
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 ******************************************************************************/
package controller.channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.table.AbstractTableModel;

import module.decode.DecoderType;
import controller.channel.Channel.ChannelType;
import controller.channel.ChannelEvent.Event;

/**
 * Channel Model
 */
public class ChannelModel extends AbstractTableModel implements IChannelEventBroadcaster
{
	private static final long serialVersionUID = 1L;

	private List<Channel> mChannels = new CopyOnWriteArrayList<>();
	private List<Channel> mTrafficChannels = new CopyOnWriteArrayList<>();
	private List<ChannelEventListener> mListeners = new CopyOnWriteArrayList<>();

	public ChannelModel()
	{
	}

	/**
	 * Unmodifiable list of non-traffic channels currently in the model
	 */
	public List<Channel> getChannels()
	{
		return Collections.unmodifiableList( mChannels );
	}
	
	public Channel getChannelAtIndex( int row )
	{
		if( mChannels.size() >= row )
		{
			return mChannels.get( row );
		}
		
		return null;
	}
	
	/**
	 * Returns a list of unique system values from across the channel set
	 */
	public List<String> getSystems()
	{
		List<String> systems = new ArrayList<>();
		
		for( Channel channel: mChannels )
		{
			if( channel.hasSystem() && !systems.contains( channel.getSystem() ) )
			{
				systems.add( channel.getSystem() );
			}
		}
		
		Collections.sort( systems );
		
		return systems;
	}
	
	/**
	 * Returns a list of unique site values for all channels that have a matching
	 * system value
	 */
	public List<String> getSites( String system )
	{
		List<String> sites = new ArrayList<>();

		if( system != null )
		{
			for( Channel channel: mChannels )
			{
				if( channel.hasSystem() && 
					system.equals( channel.getSystem() ) &&
					channel.hasSite() &&
					!sites.contains( channel.getSite() ) )
				{
					sites.add( channel.getSite() );
				}
			}
		}

		Collections.sort( sites );
		
		return sites;
	}

	/**
	 * Broadcasts the channel event to all registered listeners
	 */
	@Override
	public void broadcast( ChannelEvent event )
	{
		for( ChannelEventListener listener: mListeners )
		{
			listener.channelChanged( event );
		}
		
		if( event.getChannel().getChannelType() == ChannelType.STANDARD )
		{
			switch( event.getEvent() )
			{
				case NOTIFICATION_CONFIGURATION_CHANGE:
				case NOTIFICATION_PROCESSING_START:
				case NOTIFICATION_PROCESSING_STOP:
				case NOTIFICATION_SELECTION_CHANGE:
					int index = mChannels.indexOf( event.getChannel() );
					fireTableRowsUpdated( index, index );
					break;
				default:
					break;
			}
		}
		
		if( event.getEvent() == Event.REQUEST_DELETE )
		{
			removeChannel( event.getChannel() );
		}
	}

	/**
	 * Bulk loading of channel list.  Each channel is added and a channel add
	 * event is broadcast.
	 */
	public void addChannels( List<Channel> channels )
	{
		for( Channel channel: channels )
		{
			addChannel( channel );
		}
	}

	/**
	 * Adds the channel to the model and broadcasts a channel add event
	 */
	public int addChannel( Channel channel )
	{
		int index = -1;
		
		switch( channel.getChannelType() )
		{
			case STANDARD:
				mChannels.add( channel );
				
				index = mChannels.size() - 1;
				
				fireTableRowsInserted( index, index );
				break;
			case TRAFFIC:
				mTrafficChannels.add( channel );
				index = mChannels.size() - 1;
				break;
			default:
				break;
		}
		
		broadcast( new ChannelEvent( channel, Event.NOTIFICATION_ADD ) );
		
		if( channel.getEnabled() )
		{
			broadcast( new ChannelEvent( channel, Event.REQUEST_ENABLE ) );
		}
		
		return index;
	}
	
	/**
	 * Removes the channel from the model and broadcasts a channel remove event
	 */
	public void removeChannel( Channel channel )
	{
		if( channel != null )
		{
			switch( channel.getChannelType() )
			{
				case STANDARD:
					int index = mChannels.indexOf( channel );
					
					mChannels.remove( channel );

					if( index >= 0 )
					{
						fireTableRowsDeleted( index, index );
					}
					break;
				case TRAFFIC:
					mTrafficChannels.remove( channel );
					break;
				default:
					break;
			}
			
			broadcast( new ChannelEvent( channel, Event.NOTIFICATION_DELETE ) );
		}
	}

	/**
	 * Returns a list of channels that fall within the frequency range
	 * 
	 * @param start frequency of the range
	 * @param stop frequency of the range
	 * 
	 * @return list of channels or an empty list if none fall within the range
	 */
	public List<Channel> getChannelsInFrequencyRange( long start, long stop )
	{
		List<Channel> channels = new ArrayList<>();
		
		for( Channel channel: mChannels )
		{
			if( channel.isWithin( start, stop ) )
			{
				channels.add( channel );
			}
		}
		
		for( Channel channel: mTrafficChannels )
		{
			if( channel.isWithin( start, stop ) )
			{
				channels.add( channel );
			}
		}
		
		return channels;
	}
	
	public void addListener( ChannelEventListener listener )
	{
		mListeners.add( listener );
	}
	
	public void removeListener( ChannelEventListener listener )
	{
		mListeners.remove( listener );
	}

	//Table Model Interface Methods - standard channels only
	@Override
	public int getRowCount()
	{
		return mChannels.size();
	}

	@Override
	public int getColumnCount()
	{
		return 6;
	}

	@Override
	public String getColumnName( int columnIndex )
	{
		switch( columnIndex )
		{
			case 0:
				return "System";
			case 1:
				return "Site";
			case 2:
				return "Name";
			case 3:
				return "Alias List";
			case 4:
				return "Source";
			case 5:
				return "Decoder";
		}
		
		return null;
	}

	@Override
	public Class<?> getColumnClass( int columnIndex )
	{
		return String.class;
	}

	@Override
	public boolean isCellEditable( int rowIndex, int columnIndex )
	{
		return false;
	}

	@Override
	public Object getValueAt( int rowIndex, int columnIndex )
	{
		Channel channel = mChannels.get( rowIndex );
		
		switch( columnIndex )
		{
			case 0:
				return channel.getSystem();
			case 1:
				return channel.getSite();
			case 2:
				return channel.getName();
			case 3:
				return channel.getAliasListName();
			case 4:
				return channel.getSourceConfiguration().getDescription();
			case 5:
				return channel.getDecodeConfiguration()
							.getDecoderType().getShortDisplayString();
		}
		
		return null;
	}

	@Override
	public void setValueAt( Object aValue, int rowIndex, int columnIndex )
	{
		throw new IllegalArgumentException( "Not yet implemented" );
	}
	
	public void createChannel( DecoderType decoderType, long frequency )
	{
		throw new UnsupportedOperationException( "Not yet implemented" );
	}
}
