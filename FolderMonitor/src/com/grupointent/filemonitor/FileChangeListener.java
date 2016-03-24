package com.grupointent.filemonitor;

import java.io.File;

public interface FileChangeListener
{

    public abstract void fileChanged(File file);
}
