/*
 * This file is part of BlueMap, licensed under the MIT License (MIT).
 *
 * Copyright (c) Blue (Lukas Rieger) <https://bluecolored.de>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.bluecolored.bluemap.common.rendermanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CombinedRenderTask<T extends RenderTask> implements RenderTask {

	private final List<T> tasks;
	private int currentTaskIndex;

	public CombinedRenderTask(Collection<T> tasks) {
		this.tasks = new ArrayList<>();
		this.tasks.addAll(tasks);
		this.currentTaskIndex = 0;
	}

	@Override
	public void doWork() throws Exception {
		T task;

		synchronized (this.tasks) {
			if (!hasMoreWork()) return;
			task = this.tasks.get(this.currentTaskIndex);

			if (!task.hasMoreWork()){
				this.currentTaskIndex++;
				return;
			}
		}

		task.doWork();
	}

	@Override
	public boolean hasMoreWork() {
		return this.currentTaskIndex < this.tasks.size();
	}

	@Override
	public double estimateProgress() {
		synchronized (this.tasks) {
			if (!hasMoreWork()) return 1;

			double total = currentTaskIndex;
			total += this.tasks.get(this.currentTaskIndex).estimateProgress();

			return total / tasks.size();
		}
	}

	@Override
	public void cancel() {
		for (T task : tasks) task.cancel();
	}

}