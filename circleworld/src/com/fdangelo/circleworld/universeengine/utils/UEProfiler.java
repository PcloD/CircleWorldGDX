package com.fdangelo.circleworld.universeengine.utils;

import com.badlogic.gdx.utils.TimeUtils;

public class UEProfiler {
	static private boolean profilerEnabled = true;

	static public void Update() {
		sampledFrames++;
		// Clear();
	}

	private static final int MAX_SAMPLES = 8192;

	static private UEProfilerSample[] samples = new UEProfilerSample[MAX_SAMPLES];
	static private int currentIndex = 0;
	static private int maxSamples = 0;
	static private int sampledFrames = 0;

	static public void Clear() {
		currentIndex = 0;
		maxSamples = 0;
		sampledFrames = 0;
	}

	static public void BeginSample(final String id) {
		if (!profilerEnabled) {
			return;
		}

		if (samples[maxSamples] == null) {
			samples[maxSamples] = new UEProfilerSample();
		}

		samples[maxSamples].id = id;
		samples[maxSamples].startNanoTime = TimeUtils.nanoTime();
		samples[maxSamples].endNanoTime = 0;
		samples[maxSamples].parent = currentIndex;

		currentIndex = maxSamples;

		maxSamples++;

		// Disable the profiler if we run out of samples
		if (maxSamples == samples.length) {
			Clear();
			profilerEnabled = false;
		}
	}

	static public void EndSample() {
		if (!profilerEnabled) {
			return;
		}

		samples[currentIndex].endNanoTime = TimeUtils.nanoTime();
		currentIndex = samples[currentIndex].parent;
	}

	static public float GetSampleTime(final String id) {
		if (!profilerEnabled) {
			return 0;
		}

		long nanoTime = 0;

		for (int i = 0; i < maxSamples; i++) {
			if (samples[i].id == id) {
				nanoTime += samples[i].endNanoTime - samples[i].startNanoTime;
			}
		}

		if (sampledFrames > 0) {
			nanoTime /= sampledFrames;
		}

		return nanoTime / 1000000000.0f;
	}
}
