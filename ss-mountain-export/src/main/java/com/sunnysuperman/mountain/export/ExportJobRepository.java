package com.sunnysuperman.mountain.export;

import com.sunnysuperman.mountain.repository.CRUDRepository;

public interface ExportJobRepository<T extends ExportJob<?>> extends CRUDRepository<T, Long> {

}
