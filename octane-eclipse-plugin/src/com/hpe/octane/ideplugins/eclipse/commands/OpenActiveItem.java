package com.hpe.octane.ideplugins.eclipse.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenActiveItem extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        HandlerUtil.getActiveWorkbenchWindow(event).close();
        return null;
    }
}