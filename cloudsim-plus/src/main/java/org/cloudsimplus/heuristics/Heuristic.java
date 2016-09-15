package org.cloudsimplus.heuristics;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

/**
 * <p>Provides the methods to be used for implementation of heuristics
 * to find solution for complex problems where the solution space
 * to search is large. These problems are usually NP-Hard ones
 * which the time to find a solution increases,
 * for instance, in exponential time. Such problems can be, for instance,
 * mapping a set of VMs to existing Hosts or mapping a set of Cloudlets
 * to VMs.
 * 
 * A heuristic implementation thus provides an approximation of
 * an optimal solution (a suboptimal solution). 
 * </p>
 * 
 * <p>Different heuristic can be implemented, such as 
 * <a href="https://en.wikipedia.org/wiki/Tabu_search">Tabu search</a>, 
 * <a href="https://en.wikipedia.org/wiki/Simulated_annealing">Simulated annealing</a>,
 * <a href="https://en.wikipedia.org/wiki/Hill_climbing">Hill climbing</a> or
 * <a href="https://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms">Ant colony optimization</a>, 
 * to name a few.</p>
 * 
 * @author Manoel Campos da Silva Filho
 * @param <T> the class of solutions the heuristic will deal with
 */
public interface Heuristic<T extends HeuristicSolution> {
    /**
     * 
     * @return true if a neighbor solution can be accepted, false otherwise
     * @see #getNeighborSolution() 
     */
    public boolean acceptNeighborSolution();  
    
    /**
     * Checks if the solution search can be stopped.
     * 
     * @return true if the solution search can be stopped, false otherwise.
     */
    public boolean stopSearch();
    
    /**
     * Updates the state of the system in order to keep looking
     * for a suboptimal solution.
     */
    public void updateSystemState();
    
    /**
     * Gets the initial solution that the heuristic will start from
     * in order to try to improve it. If not initial solution was 
     * generated yet, one should be randomly generated.
     * @return the initial randomly generated solution
     */
    public T getInitialSolution();
    
    /**
     * Based on the {@link #getCurrentSolution() current solution}
     * checks if a just created neighbor solution is better than the
     * current one, updating both current and neighbor ones.
     */
    public void findNextSolution();
    
    /**
     * 
     * @return current solution
     */
    public T getCurrentSolution();
    
    /**
     * 
     * @return latest neighbor solution created
     * @see #findNextSolution() 
     */
    public T getNeighborSolution();
    
    /**
     * 
     * @return best solution that has found up to now
     */
    public T bestSolutionSoFar();
    
    /**
     * 
     * @return a random number generator
     */
    public ContinuousDistribution getRandom();
    
    /**
     * 
     * @return the current system state, that depends
     * of the heuristic implementation.
     */
    public double getCurrentState();
    
    /**
     * Sets a solution as the current one.
     * @param solution the solution to set as the current one.
     */
    public void setCurrentSolution(T solution);
    
    public T generatesRandomSolution();
    
    /**
     * Gets a random number between 0 (inclusive) and maxValue (exclusive).
     * 
     * @param maxValue the max value to get a random number (exclusive)
     * @return the random number
     */
    public default int getRandomValue(int maxValue){
        final double uniform = getRandom().sample();
        /*always get an index between [0 and size[,
        regardless if the random number generator returns
        values between [0 and 1[ or >= 1*/
        return (int)(uniform >= 1 ? uniform % maxValue : uniform * maxValue);        
    }
    
    /**
     * Starts the heuristic to find a suboptimal solution.
     * After the method finishes, call the {@link #bestSolutionSoFar()}
     * to get the final solution.
     * 
     * @return the time spent in solution finding (in seconds)
     * @see #bestSolutionSoFar() 
     */
    public default double solve() {
        long startTime = System.currentTimeMillis();
        setCurrentSolution(getInitialSolution());
        while (!stopSearch()) {
            findNextSolution();
            updateSystemState();
        }

        return (System.currentTimeMillis() - startTime)/1000.0;
    }
   
    
    /**
     * A property that implements the Null Object Design Pattern for {@link Heuristic}
     * objects.
     */
    public static final Heuristic NULL = new HeuristicNull();
}

/**
 * A class to allow the implementation of Null Object Design Pattern
 * for this interface and extensions of it.
 */
class HeuristicNull<T extends HeuristicSolution> implements Heuristic<T> {
    @Override public boolean acceptNeighborSolution() { return false; }
    @Override public boolean stopSearch() { return false; }
    @Override public void updateSystemState() {}
    @Override public T getInitialSolution() { return (T)HeuristicSolution.NULL; }
    @Override public void findNextSolution() {}
    @Override public T getCurrentSolution() { return (T)HeuristicSolution.NULL; }
    @Override public T getNeighborSolution() { return (T)HeuristicSolution.NULL; }
    @Override public T bestSolutionSoFar() { return (T)HeuristicSolution.NULL; }
    @Override public ContinuousDistribution getRandom() { return ContinuousDistribution.NULL; }
    @Override public double getCurrentState() { return 0.0; }
    @Override public void setCurrentSolution(HeuristicSolution solution) {}
    @Override public T generatesRandomSolution() { return (T)HeuristicSolution.NULL; }
}