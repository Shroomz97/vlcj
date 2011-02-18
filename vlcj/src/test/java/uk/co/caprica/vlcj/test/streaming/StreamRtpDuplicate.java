/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2009, 2010, 2011 Caprica Software Limited.
 */

package uk.co.caprica.vlcj.test.streaming;

import java.awt.Canvas;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.binding.LibX11;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

/**
 * An example of how to stream a media file using RTP and use the "duplicate"
 * output to also display the video in an embedded media player.
 * <p>
 * The client specifies an MRL of <code>rtp://@230.0.0.1:5555</code>
 */
public class StreamRtpDuplicate {

  public static void main(String[] args) throws Exception {
    LibX11.INSTANCE.XInitThreads();
    
    String media = "/home/movies/test.mp4";
    String options = formatRtpStream("230.0.0.1", 5555);

    System.out.println("Streaming '" + media + "' to '" + options + "'");
    
    MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(args);
    EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
    
    Canvas canvas = new Canvas();
    CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
    mediaPlayer.setVideoSurface(videoSurface);
    
    JFrame f = new JFrame();
    f.add(canvas);
    f.setSize(800, 600);
    f.setVisible(true);
    
    mediaPlayer.playMedia(media,
      options,
      ":no-sout-rtp-sap", 
      ":no-sout-standard-sap", 
      ":sout-all", 
      ":sout-keep"
    );
    
    // Don't exit
    Thread.currentThread().join();
  }
  
  private static String formatRtpStream(String serverAddress, int serverPort) {
    StringBuilder sb = new StringBuilder(60);
    sb.append(":sout=#duplicate{dst=display,dst=rtp{dst=");
    sb.append(serverAddress);
    sb.append(",port=");
    sb.append(serverPort);
    sb.append(",mux=ts}}");
    return sb.toString();
  }
}