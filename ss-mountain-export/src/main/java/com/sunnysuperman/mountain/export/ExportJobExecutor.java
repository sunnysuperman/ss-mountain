package com.sunnysuperman.mountain.export;

public interface ExportJobExecutor<T extends ExportJob<?>> {

	void execute(T job);

}
