package org.fitz.util;

import java.util.ArrayList;
import java.util.Comparator;

/**This class is used to implement a custom sorting for continuous values
 * in a dataset
 * Created by FitzRoi on 2/19/16.
 */
public class DataSorter implements Comparator<ArrayList<String>> {
    public int sortingIndex = 0;
    @Override
    public int compare(ArrayList<String> row1, ArrayList<String> row2) {
        Double o1 = new Double(row1.get(sortingIndex));
        Double o2 = new Double(row2.get(sortingIndex));

        return o1.compareTo(o2);
    }

    public void setSortingIndex(int index) {
        this.sortingIndex = index;
    }
}
