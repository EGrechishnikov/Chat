package dao;

import bean.Message;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgeniy Grechishnikov
 */
public class MessageDAOTest {
    private static MessageDAO dao = new MessageDAO();

    @Test
    public void test() {
        List<Integer> list = new ArrayList<Integer>();
        int size = dao.getAllWithoutMyMessages(list, 0).size();
        Message message = new Message("test", "Tester");
        message.setId(dao.save(message));
        Assert.assertNotEquals(size, dao.getAllWithoutMyMessages(list, 0).size());
        list.add(message.getId());
        Assert.assertEquals(size, dao.getAllWithoutMyMessages(list, 0).size());
    }
}
