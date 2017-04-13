package Utilities;

import java.util.List;
import java.util.Set;

/**
 * Created by homosapien97 on 4/7/17.
 */
public interface Dependent {
    Set<Dependable> dependencies();
    void update(Dependable updater, Update update);
}
