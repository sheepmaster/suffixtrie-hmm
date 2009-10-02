package de.tum.in.lrr.hmm.util;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class AsynchronousMapper<A, B> implements Mapper<A, B> {

    public Iterator<B> mapDirect(final Function<A, B> f, final Iterator<A> as) {
        return new TransformingIterator<A,B>(as) {
            @Override
            public B transform(A a) {
                return f.call(a);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<B> map(final Function<A, B> f, Iterator<A> as) {
        final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        final Queue<Future<B>> futures = new ArrayDeque<Future<B>>();

        while (as.hasNext()) {
            final A a = as.next();
            futures.add(executor.submit(new Callable<B>() {
                public B call() throws Exception {
                    return f.call(a);
                }
            }));
        }
        return new UnmodifiableIterator<B>() {
            public boolean hasNext() {
                return !futures.isEmpty();
            }

            public B next() {
                try {
                    return futures.remove().get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public Iterator<B> mapAnyOrder(final Function<A, B> f, Iterator<A> as) {
        final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        final BlockingQueue<B> queue = new LinkedBlockingQueue<B>();

        while (as.hasNext()) {
            final A a = as.next();
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        queue.put(f.call(a));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        executor.shutdown();

        return new UnmodifiableIterator<B>() {
            public boolean hasNext() {
                return !executor.isTerminated();
            }

            public B next() {
                try {
                    return queue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
