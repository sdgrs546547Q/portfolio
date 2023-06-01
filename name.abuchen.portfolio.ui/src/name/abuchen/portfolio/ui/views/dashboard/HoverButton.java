package name.abuchen.portfolio.ui.views.dashboard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import name.abuchen.portfolio.ui.Images;
import name.abuchen.portfolio.ui.util.Colors;

/**
 * Displays a button when the mouse moves into the given controls that allows
 * the user to click a hyperlink. The button is used for example to jump from
 * the dashboard to the underlying view.
 */
public class HoverButton
{
    private Control leadingControl;
    private IHyperlinkListener listener;

    private Shell hoverShell;

    private HoverButton(Control leadingControl, Control... others)
    {
        this.leadingControl = leadingControl;

        this.leadingControl.addListener(SWT.MouseEnter, this::onMouseEnter);
        this.leadingControl.addListener(SWT.MouseExit, this::onMouseExit);

        this.leadingControl.addDisposeListener(e -> {
            if (hoverShell != null)
                hoverShell.dispose();
        });

        for (Control c : others)
        {
            c.addListener(SWT.MouseEnter, this::onMouseEnter);
            c.addListener(SWT.MouseExit, this::onMouseExit);
        }
    }

    public static HoverButton build(Control leadingControl, Control... others)
    {
        return new HoverButton(leadingControl, others);
    }

    public HoverButton withListener(IHyperlinkListener listener)
    {
        this.listener = listener;
        return this;
    }

    private void onMouseExit(Event event)
    {
        if (hoverShell != null && hoverShell.isVisible())
        {
            hoverShell.setVisible(false);
        }
    }

    private void onMouseEnter(Event evnet)
    {
        if (hoverShell == null)
        {
            hoverShell = new Shell(leadingControl.getShell(), SWT.MODELESS | SWT.SHADOW_OUT);
            hoverShell.setBackground(Colors.WHITE);
            hoverShell.setLayout(new FillLayout());
            ImageHyperlink button = new ImageHyperlink(hoverShell, SWT.NONE);
            button.setImage(Images.VIEW_SHARE.image());
            button.setBackground(Colors.WHITE);
            button.addHyperlinkListener(listener);
            hoverShell.pack();

            // we must not close the hover button if existing the underlying
            // controls because the mouse is entering the hover button; register
            // only the enter listener to avoid flickering (which, however,
            // leaves the shell visible in some cases)
            hoverShell.addListener(SWT.MouseEnter, this::onMouseEnter);
            button.addListener(SWT.MouseEnter, this::onMouseEnter);
        }

        if (!hoverShell.isVisible())
        {
            Rectangle controlBounds = leadingControl.getBounds();
            Rectangle hoverBounds = hoverShell.getBounds();

            int locationX = controlBounds.x + controlBounds.width - hoverBounds.width;
            int locationY = controlBounds.y;
            hoverShell.setLocation(leadingControl.toDisplay(locationX, locationY));

            hoverShell.setVisible(true);
        }

    }
}