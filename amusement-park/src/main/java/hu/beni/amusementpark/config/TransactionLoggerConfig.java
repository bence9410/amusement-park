package hu.beni.amusementpark.config;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hu.beni.amusementpark.service.impl.AmusementParkServiceImpl;
import hu.beni.amusementpark.service.impl.GuestBookRegistryServiceImpl;
import hu.beni.amusementpark.service.impl.MachineServiceImpl;
import hu.beni.amusementpark.service.impl.VisitorServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class TransactionLoggerConfig {

	private static final Set<Class<?>> targetClasses;

	static {
		targetClasses = new HashSet<>();
		targetClasses.add(AmusementParkServiceImpl.class);
		targetClasses.add(MachineServiceImpl.class);
		targetClasses.add(VisitorServiceImpl.class);
		targetClasses.add(GuestBookRegistryServiceImpl.class);
	}

	@Bean
	@ConditionalOnProperty("amusement-park.transaction-logging")
	public DefaultPointcutAdvisor transactionLoggerAdvisor() {
		return new DefaultPointcutAdvisor(createPointcut(), createMethodInterceptor());
	}

	private Pointcut createPointcut() {
		return new Pointcut() {

			@Override
			public MethodMatcher getMethodMatcher() {
				return createMethodMatcher();
			}

			@Override
			public ClassFilter getClassFilter() {
				return targetClasses::contains;
			}
		};
	}

	private MethodMatcher createMethodMatcher() {
		return new MethodMatcher() {

			@Override
			public boolean matches(Method method, Class<?> targetClass, Object... args) {
				return true;
			}

			@Override
			public boolean matches(Method method, Class<?> targetClass) {
				return true;
			}

			@Override
			public boolean isRuntime() {
				return false;
			}
		};
	}

	private MethodInterceptor createMethodInterceptor() {
		return methodInvocation -> {
			String transactionId = UUID.randomUUID().toString();
			long start = System.currentTimeMillis();
			log.info(transactionId + ": " + methodInvocation.getThis().getClass().getSimpleName() + "."
					+ methodInvocation.getMethod().getName() + " started with parameters: "
					+ Stream.of(methodInvocation.getArguments()).map(Object::toString).collect(Collectors.toList()));
			try {
				Object obj = methodInvocation.proceed();
				logResult(transactionId, methodInvocation, start, "successfully");
				return obj;
			} catch (Throwable t) {
				logResult(transactionId, methodInvocation, start, "exceptionally");
				throw t;
			}
		};
	}

	private void logResult(String transactionId, MethodInvocation methodInvocation, long start, String result) {
		log.info(transactionId + ": " + methodInvocation.getThis().getClass().getSimpleName() + "."
				+ methodInvocation.getMethod().getName() + " completed " + result + " in "
				+ Long.toString(System.currentTimeMillis() - start) + "ms.");
	}

}
