package com.hpe.octane.ideplugins.eclipse.ui.editor.snake;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * This is only an editor so it can only be opened via the code <br>
 * Intended to be an easter egg
 */
public class SnakeEditor extends EditorPart {

    public static final String ID = "com.hpe.octane.ideplugins.eclipse.ui.editor.snake.SnakeEditor"; //$NON-NLS-1$
    private SnakeGameCanvas snakeGameCanvas;

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
    }

    /**
     * Create contents of the editor part.
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        snakeGameCanvas = new SnakeGameCanvas(parent);
    }

    @Override
    public void setFocus() {
        if (snakeGameCanvas != null) {
            snakeGameCanvas.setFocus();
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

}