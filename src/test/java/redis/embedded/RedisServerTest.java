package redis.embedded;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.embedded.exceptions.RedisBuildingException;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RedisServerTest {

    private RedisServer redisServer;

    @Test public void testSystemVersion(){
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        System.out.println(osName);
        System.out.println(osArch);
        System.out.println(System.getenv("PROCESSOR_ARCHITECTURE"));
    }

    @Test
    @Timeout(unit = TimeUnit.MILLISECONDS, value = 1500L)
    public void testSimpleRun() throws Exception {
        try {
            redisServer = new RedisServer(6380);
            redisServer.start();
            Thread.sleep(1000L);
        }finally {
            redisServer.stop();
        }
    }

    @Test
    public void shouldNotAllowMultipleRunsWithoutStop() {
        assertThrows(RuntimeException.class, () -> {
            try {
                redisServer = new RedisServer(6379);
                redisServer.start();
                redisServer.start();
            } finally {
                redisServer.stop();
            }
        });
    }

    @Test
    public void shouldAllowSubsequentRuns() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();
        redisServer.stop();

        redisServer.start();
        redisServer.stop();

        redisServer.start();
        redisServer.stop();
    }

    @Test
    public void testSimpleOperationsAfterRun() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();

        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = new JedisPool("localhost", 6379);
            jedis = pool.getResource();
            jedis.mset("abc", "1", "def", "2");

            assertEquals("1", jedis.mget("abc").get(0));
            assertEquals("2", jedis.mget("def").get(0));
            Assertions.assertNull(jedis.mget("xyz").get(0));
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
            redisServer.stop();
        }
    }

    @Test
    public void shouldIndicateInactiveBeforeStart() throws Exception {
        redisServer = new RedisServer(6379);
        Assertions.assertFalse(redisServer.isActive());
    }

    @Test
    public void shouldIndicateActiveAfterStart() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();
        Assertions.assertTrue(redisServer.isActive());
        redisServer.stop();
    }

    @Test
    public void shouldIndicateInactiveAfterStop() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();
        redisServer.stop();
        Assertions.assertFalse(redisServer.isActive());
    }

    @Test
    public void shouldOverrideDefaultExecutable() {
        RedisExecProvider customProvider = RedisExecProvider.defaultProvider()
                .override(OS.UNIX, Architecture.x86, Resources.getResource("redis-server-2.8.19-32").getFile())
                .override(OS.UNIX, Architecture.x86_64, Resources.getResource("redis-server-2.8.19").getFile())
                .override(OS.WINDOWS, Architecture.x86, Resources.getResource("redis-server-2.8.19.exe").getFile())
                .override(OS.WINDOWS, Architecture.x86_64, Resources.getResource("redis-server-2.8.19.exe").getFile())
                .override(OS.MAC_OS_X, Resources.getResource("redis-server-2.8.19").getFile());

        redisServer = new RedisServerBuilder()
                .redisExecProvider(customProvider)
                .build();
    }

    @Test
    public void shouldFailWhenBadExecutableGiven() {
        assertThrows(RedisBuildingException.class, () -> {
            RedisExecProvider buggyProvider = RedisExecProvider.defaultProvider()
                    .override(OS.UNIX, "some")
                    .override(OS.WINDOWS, Architecture.x86, "some")
                    .override(OS.WINDOWS, Architecture.x86_64, "some")
                    .override(OS.MAC_OS_X, "some");

            redisServer = new RedisServerBuilder()
                    .redisExecProvider(buggyProvider)
                    .build();
        });
    }
}
