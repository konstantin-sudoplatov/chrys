package concepts.dyn.ifaces;

/**
 * It is a marker interface. When implemented, the concept becomes global, i.e. cannot be cloned from the AttnDispatcherLoop
 * name space into caldrons and will always be a singleton. The get_cpt() methods in any name space, caldron or attention dispatcher,
 * will return that concept from the attention dispatcher, which is a global structure for the project.
 * @author su
 */
public interface GlobalConcept {}
