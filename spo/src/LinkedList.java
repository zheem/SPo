import java.util.ArrayList;
import java.util.List;

public class LinkedList {
    private ListItem firstItem = null;
    private ListItem lastItem = null;

    public void add (int value) {
        ListItem item = new ListItem(value);

        if (firstItem == null) {
            firstItem = item;
        } else {
            ListItem nItem = firstItem; // используется для итерации и поиска последнего

            while (true) {
                if (nItem.nextItemLink == null)
                    break;

                nItem = nItem.nextItemLink; // итерируемся. На выходе будет последний элемент списка
            }

            nItem.nextItemLink = item;
            item.previousItemLink = nItem;
        }
    }

    public int getByIndex (int index) {
        if (index == 0)
            return firstItem.value;

        int counter = 0;
        ListItem nItem = firstItem;

        while (counter < index) {
            if (nItem.nextItemLink == null)
                break;

            nItem = nItem.nextItemLink;
            counter++;
        }

        return  nItem.value;
    }
    public void delete (int index) {
        if (index == 0){
            ListItem nItem = firstItem.nextItemLink;
            firstItem = nItem;
            firstItem.previousItemLink = null;
        }
        else {
            int counter = 0;
            ListItem nItem = firstItem;
            while (counter < index) {
                if (nItem.nextItemLink == null)
                    break;

                nItem = nItem.nextItemLink;
                counter++;
            }
            nItem.previousItemLink.nextItemLink = nItem.nextItemLink;
            nItem.nextItemLink.previousItemLink = nItem.previousItemLink;
        }
    }
}