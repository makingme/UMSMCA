package kr.uracle.ums.agent.proccess;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.uracle.ums.agent.core.TaskWorker;

public class WorkerManager extends Thread{
	
	private static final Logger log = LoggerFactory.getLogger(WorkerManager.class);


	private static ConcurrentHashMap<TaskWorker, Thread> workers=new ConcurrentHashMap<TaskWorker, Thread>();
	
	
	@Override
	public void run() {
		log.info("시스템 종료 요청에 의한 종료.....");
		
	}
	
	public static void main(String[] args) {
		String name = args.length > 0 ? args[0] : "";
		Runtime.getRuntime().addShutdownHook(new WorkerManager());
	}
}
