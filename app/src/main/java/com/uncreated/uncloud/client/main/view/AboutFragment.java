package com.uncreated.uncloud.client.main.view;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uncreated.uncloud.R;

public class AboutFragment
		extends Fragment
{
	public AboutFragment()
	{

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_about, container, false);
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
	}
}
