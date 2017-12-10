package bridge.agent;

import com.sun.management.OperatingSystemMXBean;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.lang.management.ManagementFactory;
import java.util.UUID;

public class Agent extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Agent());
  }

  @Override
  public void start() {
    OperatingSystemMXBean systemMBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    String pid = UUID.randomUUID().toString();
    vertx.setPeriodic(1000, id -> {
      JsonObject metrics = new JsonObject();
      metrics.put("CPU", systemMBean.getProcessCpuLoad());
      metrics.put("Mem", systemMBean.getTotalPhysicalMemorySize() - systemMBean.getFreePhysicalMemorySize());
      vertx.eventBus().publish("metrics", new JsonObject().put(pid, metrics));
    });
  }
}
