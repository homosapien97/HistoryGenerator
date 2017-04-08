package Utilities;

import java.util.List;

/**
 * Created by homosapien97 on 4/7/17.
 */
public interface Dependent {
    List<Object> dependencies();
    void update();
}
