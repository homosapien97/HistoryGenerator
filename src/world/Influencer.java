package world;

import utilities.Centralizable;

/**
 * Created by homosapien97 on 4/15/17.
 */
public interface Influencer {
    <T extends Centralizable & Influencable> boolean influence(T c);
}
