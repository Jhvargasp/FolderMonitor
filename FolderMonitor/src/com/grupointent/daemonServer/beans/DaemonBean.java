package com.grupointent.daemonServer.beans;

import java.io.PrintStream;

public class DaemonBean {

	private int timeEnlapsed;
	private String classToLaunch;
	private String name;

	public DaemonBean() {
		System.out.println("Log......");
	}

	public String getClassToLaunch() {
		return classToLaunch;
	}

	public void setClassToLaunch(String classToLaunch) {
		this.classToLaunch = classToLaunch;
	}

	public int getTimeEnlapsed() {
		return timeEnlapsed;
	}

	public void setTimeEnlapsed(int timeLapsed) {
		timeEnlapsed = timeLapsed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
