package io.vusion.vtransmit.v2.commons.utils;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	
	@Transactional
	public void manageTransaction(Runnable runnable) {
		runnable.run();
	}
	
	@Transactional
	public <V> V manageTransaction(final RunnableWithReturn<V> consumer) {
		return consumer.run();
	}
	
	public static interface RunnableWithReturn<V> {
		V run();
	}
}
