package de.blacksheepsoftware.hmm;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class BatchTrainer extends Trainable {

    protected Model model;

    protected Model newModel;

    public BatchTrainer(Model m) {
        replaceModel(m);
    }

    @Override
    public void learn(int[] w, int maxDepth, int linearThreshold) {
        final StateDistribution startingDistribution = model.startingDistribution();
        newModel.learn(w, startingDistribution, startingDistribution, 0, w.length+1, maxDepth, linearThreshold);
    }

    public void finishBatch() {
        replaceModel(newModel);
    }

    protected void replaceModel(Model m) {
        model = m;
        newModel = new Model(m);
        newModel.copyTransitionsFrom(m);
    }

    public Model getModel() {
        return newModel;
    }

}
