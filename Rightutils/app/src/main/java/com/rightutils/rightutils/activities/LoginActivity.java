package com.rightutils.rightutils.activities;

/**
 * Created by Anton Maniskevich on 8/18/14.
 */
public interface LoginActivity<T> {

	void sendRequest();

	void doStart(T element);
}
