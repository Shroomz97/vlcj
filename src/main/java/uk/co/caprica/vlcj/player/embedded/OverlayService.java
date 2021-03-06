package uk.co.caprica.vlcj.player.embedded;

import uk.co.caprica.vlcj.player.embedded.videosurface.ComponentVideoSurface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// FIXME overlay should be disabled depending on the type of videosurface - we need to document that somewhere
// FIXME need to make sure we only enable the overlay, add listeners etc if the videosurface supports it, that way we don't have a bunch of null checks all over the place

public final class OverlayService extends BaseService {

    /**
     * Listener implementation used to keep the overlay position and size in sync with the video
     * surface.
     */
    private final OverlayComponentAdapter overlayComponentAdapter = new OverlayComponentAdapter();

    /**
     * Listener implementation used to keep the overlay visibility state in sync with the video
     * surface.
     */
    private final OverlayWindowAdapter overlayWindowAdapter = new OverlayWindowAdapter();

    /**
     *
     */
    private final Rectangle bounds = new Rectangle();

    /**
     * Optional overlay component.
     */
    private Window overlay;

    /**
     * Track the requested overlay enabled/disabled state so it can be restored when needed.
     */
    private boolean requestedOverlay;

    /**
     * Track whether or not the overlay should be restored when the video surface is shown/hidden.
     */
    private boolean restoreOverlay;

    OverlayService(DefaultEmbeddedMediaPlayer mediaPlayer) {
        super(mediaPlayer);
    }

    /**
     * Get the overlay component.
     *
     * @return overlay component, may be <code>null</code>
     */
    public Window getOverlay() {
        return overlay;
    }

    /**
     * Set a new overlay component.
     * <p>
     * The existing overlay if there is one will be disabled.
     * <p>
     * The new overlay will <strong>not</strong> automatically be enabled.
     * <p>
     * The overlay should be a sub-class of <code>Window</code> or <code>JWindow</code>. If your
     * overlay contains dynamically updated content such as a timer or animated graphics, then you
     * should use <code>JWindow</code> so that your updates will be double-buffered and there will
     * be no tearing or flickering when you paint the overlay. If you do this, you must take care to
     * erase the overlay background before you paint it.
     * <p>
     * <strong>When the overlay is no longer needed it is your responsibility to {@link Window#dispose()}
     * it - if you do not do this you may leak resources. If you set multiple different overlays you
     * must remember to dispose the old overlay.</strong>
     * <p>
     * It is recommended to set the overlay once as early as possible in your application.
     *
     * @param overlay overlay component, may be <code>null</code>
     */
    public void setOverlay(Window overlay) {
        if (mediaPlayer.videoSurface().getVideoSurface() instanceof ComponentVideoSurface) {
            // Disable the current overlay if there is one
            enableOverlay(false);
            // Remove the existing overlay if there is one
            removeOverlay();
            // Add the new overlay, but do not enable it
            addOverlay(overlay);
        }
        else {
            throw new IllegalStateException("Can't set an overlay when there's no video surface");
        }
    }

    /**
     * Enable/disable the overlay component if there is one.
     *
     * @param enable whether to enable the overlay or disable it
     */
    public void enableOverlay(boolean enable) {
        requestedOverlay = enable;
        if (overlay != null) {
            if (enable) {
                if (!overlay.isVisible()) {
                    Component component = getComponent();
                    component.getBounds(bounds);
                    bounds.setLocation(component.getLocationOnScreen());
                    overlay.setBounds(bounds);
                    Window window = getAncestorWindow(component);
                    window.addComponentListener(overlayComponentAdapter);
                    overlay.setVisible(true);
                }
            }
            else {
                if (overlay.isVisible()) {
                    overlay.setVisible(false);
                    Window window = getAncestorWindow(getComponent());
                    window.removeComponentListener(overlayComponentAdapter);
                }
            }
        }
    }

    /**
     * Check whether or not there is an overlay component currently enabled.
     *
     * @return true if there is an overlay enabled, otherwise false
     */
    public boolean overlayEnabled() {
        return overlay != null && overlay.isVisible();
    }

    /**
     * Install an overlay component.
     *
     * @param overlay overlay window
     */
    private void addOverlay(Window overlay) {
        if (overlay != null) {
            this.overlay = overlay;
            Window window = getAncestorWindow(getComponent());
            if (window != null) {
                window.addWindowListener(overlayWindowAdapter);
            }
            else {
                // This should not be possible
//                logger.warn("Failed to find a Window ancestor for the video surface Canvas");
            }
        }
    }

    /**
     * Remove the overlay component.
     */
    private void removeOverlay() {
        if (overlay != null) {
            Window window = getAncestorWindow(getComponent());
            window.removeWindowListener(overlayWindowAdapter);
            overlay = null;
        }
    }

    /**
     * Component event listener to keep the overlay component in sync with the video surface component.
     * <p>
     * This adapter will only be used if there is a valid component-based video surface, so we can forego some checks.
     */
    private final class OverlayComponentAdapter extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            overlay.setSize(getComponent().getSize());
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            overlay.setLocation(getComponent().getLocationOnScreen());
        }

        @Override
        public void componentShown(ComponentEvent e) {
            showOverlay();
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            hideOverlay();
        }
    }

    /**
     * Window event listener to hide the overlay when the video window is hidden, and vice versa.
     */
    private final class OverlayWindowAdapter extends WindowAdapter {

        @Override
        public void windowIconified(WindowEvent e) {
            // Nothing, this is taken care of by "windowDeactivated"
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            showOverlay();
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            hideOverlay();
        }

        @Override
        public void windowActivated(WindowEvent e) {
            showOverlay();
        }
    }

    /**
     * Make the overlay visible.
     */
    private void showOverlay() {
        if (restoreOverlay) {
            enableOverlay(true);
        }
    }

    /**
     * Hide the overlay.
     */
    private void hideOverlay() {
        if (requestedOverlay) {
            restoreOverlay = true;
            enableOverlay(false);
        }
        else {
            restoreOverlay = false;
        }
    }

    /**
     * This method will only be used if the video surface is a {@link ComponentVideoSurface} so we can forego some
     * checks.
     *
     * @return
     */
    private Component getComponent() {
        return ((ComponentVideoSurface) mediaPlayer.videoSurface().getVideoSurface()).component();
    }

    /**
     * This method will only be used if the video surface is a {@link ComponentVideoSurface} so we can forego some
     * checks.
     *
     * @param component
     * @return
     */
    private Window getAncestorWindow(Component component) {
        return (Window)SwingUtilities.getAncestorOfClass(Window.class, component);
    }

}
