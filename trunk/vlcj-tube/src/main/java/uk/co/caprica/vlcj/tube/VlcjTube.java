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

package uk.co.caprica.vlcj.tube;

import java.awt.Canvas;
import java.awt.Frame;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

/**
 * An application that embeds a native Web Browser component and a native
 * media player component.
 * <p>
 * Activating a video hyperlink causes the video to be played by the embedded
 * native media player.
 * <p>
 * Press the F11 key to toggle full-screen mode.
 * <p>
 * Press the ESCAPE key to quit the current video and return to the browser.
 * <p>
 * SWT has some focus issues, you may need to click the mouse to return focus
 * to the window before the key-bindings have an effect.
 * <p> 
 * WebKit is a better choice than Mozilla for the embedded browser.
 * <pre>
 *   -Dorg.eclipse.swt.browser.UseWebKitGTK=true
 * </pre>
 * With SWT 3.7, WebKit (if available) is the default implementation and the 
 * above property is not required. 
 */
public class VlcjTube {

  /**
   * Initial URL.
   */
  private static final String HOME_URL = "http://www.youtube.com";
  
  /**
   * URLs matching this pattern will be intercepted and played by the embedded
   * media player.
   */
  private static final String WATCH_VIDEO_PATTERN = "http://www.youtube.com/watch\\?v=.*";
  
  /**
   * Pre-compiled regular expression pattern.
   */
  private Pattern watchLinkPattern;

  /**
   * UI components.
   */
  private Display display;
  private Shell shell;
  private StackLayout stackLayout;
  private Composite browserPanel;
  private Browser browser;
  private Composite videoPanel;
  private Composite videoComposite;
  private Frame videoFrame;
  private Canvas videoSurfaceCanvas;
  private CanvasVideoSurface videoSurface;
  private Cursor emptyCursor;

  /**
   * Native media player components.
   */
  private MediaPlayerFactory mediaPlayerFactory;
  private EmbeddedMediaPlayer mediaPlayer;
  
  /**
   * Application entry point.
   * 
   * @param args command-line arguments
   * @throws Exception if an error occurs
   */
  public static void main(String[] args) throws Exception {
    new VlcjTube().start();
  }
  
  /**
   * Create an application.
   * 
   * @throws Exception if an error occurs
   */
  public VlcjTube() throws Exception {
    watchLinkPattern = Pattern.compile(WATCH_VIDEO_PATTERN);
    
    createUserInterface();
    createEmptyCursor();
    createMediaPlayer();
  }
  
  /**
   * Create the user interface controls.
   */
  private void createUserInterface() {
    display = new Display();
    
    stackLayout = new StackLayout();

    shell = new Shell(display);
    shell.setLayout(stackLayout);

    browserPanel = new Composite(shell, SWT.BORDER);
    browserPanel.setLayout(new FillLayout());
    browserPanel.setBackground(display.getSystemColor(SWT.COLOR_BLUE));

    browser = new Browser(browserPanel, SWT.NONE);
    browser.setJavascriptEnabled(true);
    
    videoPanel = new Composite(shell, SWT.BORDER);
    videoPanel.setLayout(new FillLayout());
    videoPanel.setBackground(display.getSystemColor(SWT.COLOR_RED));
    
    videoComposite = new Composite(videoPanel, SWT.EMBEDDED | SWT.NO_BACKGROUND);
    videoComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
    videoFrame = SWT_AWT.new_Frame(videoComposite);
    videoSurfaceCanvas = new Canvas();
    videoSurfaceCanvas.setBackground(java.awt.Color.black);
    videoFrame.add(videoSurfaceCanvas);

    showBrowser();
    
    shell.addShellListener(new ShellAdapter() {
      @Override
      public void shellClosed(ShellEvent arg0) {
        mediaPlayer.release();
        mediaPlayerFactory.release();
      }
    });
    
    browser.addTitleListener(new TitleListener() {
      public void changed(TitleEvent e) {
        shell.setText(e.title);
      }
    });
    
    browser.addLocationListener(new LocationAdapter() {
      @Override
      public void changing(LocationEvent evt) {
        Matcher matcher = watchLinkPattern.matcher(evt.location);
        if(matcher.matches()) {
          evt.doit = false;
          System.out.println("new movie: " + evt.location);
          showVideo();
          mediaPlayer.playMedia(evt.location);
        }
      }
    });
    
    browser.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        switch(e.keyCode) {
          case SWT.F11:
            shell.setFullScreen(!shell.getFullScreen());
            break;
        }
      }
    });
    
    videoComposite.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        switch(e.keyCode) {
          case SWT.ESC:
            mediaPlayer.stop();
            showBrowser();
            break;

          case SWT.F11:
            shell.setFullScreen(!shell.getFullScreen());
            break;
        }
      }
    });
    
    shell.open();
  }
  
  /**
   * Create an empty cursor.
   */
  private void createEmptyCursor() {
    Color white = display.getSystemColor(SWT.COLOR_WHITE);
    Color black = display.getSystemColor(SWT.COLOR_BLACK);
    PaletteData palette = new PaletteData(new RGB[] { white.getRGB(), black.getRGB() });
    ImageData sourceData = new ImageData(16, 16, 1, palette);
    sourceData.transparentPixel = 0;
    emptyCursor = new Cursor(display, sourceData, 0, 0);
  }
  
  /**
   * Create the native media player components.
   */
  private void createMediaPlayer() {
    mediaPlayerFactory = new MediaPlayerFactory();
    mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
    mediaPlayer.setPlaySubItems(true);
    videoSurface = mediaPlayerFactory.newVideoSurface(videoSurfaceCanvas);
    mediaPlayer.setVideoSurface(videoSurface);
    
    mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
      @Override
      public void opening(MediaPlayer mediaPlayer) {
        System.out.println("opening");
        // Similar to Swing, obey the SWT threading model...
        display.asyncExec(new Runnable() {
          public void run() {
            showVideo();
          }
        });
      }
      
      @Override
      public void finished(MediaPlayer mediaPlayer) {
        System.out.println("finished");
        // Similar to Swing, obey the SWT threading model...
        display.asyncExec(new Runnable() {
          public void run() {
            showBrowser();
          }
        });
      }
    });
  }
  
  /**
   * Start the application.
   * <p>
   * Execute the SWT message loop.
   */
  private void start() {
    browser.setUrl(HOME_URL);

    while(!shell.isDisposed()) {
      if(!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }
  
  private void showBrowser() {
    showView(browserPanel);
  }
  
  private void showVideo() {
    showView(videoPanel);
  }
  
  private void showView(Composite view) {
    stackLayout.topControl = view;
    shell.layout();
  }
}