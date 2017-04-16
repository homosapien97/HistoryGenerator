package utilities;

/**
 * Created by homosapien97 on 4/7/17.
 */
public interface Dependable {
    public void addDependent(Dependent dependent);
    public void updateDependents();
}
