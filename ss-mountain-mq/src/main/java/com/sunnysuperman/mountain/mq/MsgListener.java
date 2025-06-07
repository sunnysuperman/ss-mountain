package com.sunnysuperman.mountain.mq;

import java.util.Properties;

public interface MsgListener {

	boolean onMessage(byte[] body, Properties headers);

}
