package com.sunnysuperman.mountain.evt;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.base.BaseProperties;
import com.sunnysuperman.mountain.job.JobUtils;
import com.sunnysuperman.mountain.lang.utils.Obj;
import com.sunnysuperman.mountain.lang.utils.Types;

public class EvtClassManager {
	private static final Logger LOG = LoggerFactory.getLogger(EvtClassManager.class);

	private EvtProperties evtProperties;
	private BaseProperties baseProperties;
	private Set<Class<?>> classes;

	public EvtClassManager(EvtProperties evtProperties, BaseProperties baseProperties) {
		super();
		this.evtProperties = evtProperties;
		this.baseProperties = baseProperties;
	}

	@PostConstruct
	public void init() {
		String[] packages = Obj.or(evtProperties.getScanPackages(), baseProperties.getScanPackages());
		classes = Types.findTypesAnnotatedWith(packages, EvtConf.class);
		Set<String> names = new HashSet<>(classes.size());
		for (Class<?> clazz : classes) {
			EvtConf conf = clazz.getAnnotation(EvtConf.class);
			try {
				validate(conf, names);
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("Event " + clazz + " error: " + ex.getMessage());
			}
		}
		names.clear();
		LOG.info(">>>>>>[evt] class-manager initialized with {} classes", classes.size());
	}

	private void validate(EvtConf conf, Set<String> names) {
		String name = conf.name();
		if (!JobUtils.isValidJobName(name)) {
			throw new IllegalArgumentException("Bad job name");
		}
		if (name.indexOf("event") >= 0) {
			LOG.warn(">>>>>>[evt] Event name should not contain 'event' !!!!!!");
		}
		if (!names.add(name)) {
			throw new IllegalArgumentException("Duplicate event name '" + name + "'");
		}
		if (conf.consumeTimeoutInSeconds() <= 0) {
			throw new IllegalArgumentException("consumeTimeoutInSeconds should > 0");
		}
	}

	public Set<Class<?>> getAll() {
		return classes;
	}
}
