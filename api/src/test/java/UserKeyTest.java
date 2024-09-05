import cc.carm.plugin.playerstats.manager.UserKeyManager;
import cc.carm.plugin.playerstats.user.UserKey;
import org.junit.Test;

import java.util.UUID;

public class UserKeyTest {

    @Test
    public void test() {

        UserKey one = UserKey.of(1, UUID.randomUUID(), "somebody");
        UserKey two = UserKey.of(2, UUID.randomUUID(), "someone");

        System.out.println(one.equals(UserKey.KeyType.ID, 1));

    }

}
