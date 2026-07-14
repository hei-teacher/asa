package school.hei.asa.concurrency;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ThreadRenamerTest {

  @Test
  void renameWorkerThread_renames_thread() {
    var thread = new Thread(() -> {});
    thread.setName("old-name");
    ThreadRenamer.renameWorkerThread(thread);
    assertTrue(thread.getName().startsWith("w-"));
    assertEquals(8, thread.getName().length());
  }

  @Test
  void renameFrontalThread_renames_thread() {
    var thread = new Thread(() -> {});
    thread.setName("old-name");
    ThreadRenamer.renameFrontalThread(thread);
    assertTrue(thread.getName().startsWith("f-"));
    assertEquals(8, thread.getName().length());
  }

  @Test
  void renameThread_changes_name() {
    var thread = new Thread(() -> {});
    thread.setName("old-name");
    ThreadRenamer.renameThread(thread, "new-name");
    assertEquals("new-name", thread.getName());
  }

  @Test
  void getRandomSubThreadNamePrefixFrom_returns_prefixed_name() {
    var parent = new Thread(() -> {});
    parent.setName("parent-thread");
    var result = ThreadRenamer.getRandomSubThreadNamePrefixFrom(parent);
    assertTrue(result.startsWith("parent-thread-"));
    assertEquals("parent-thread-".length() + 6, result.length());
  }
}
