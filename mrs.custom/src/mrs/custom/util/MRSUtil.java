package mrs.custom.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.widgets.Shell;

import mrs.Layer;
import mrs.Metamodel;
import mrs.ModularReferenceStructure;
import mrs.MrsFactory;

public class MRSUtil {

    /**
     * Returns the main {@link EPackage} at the specified {@link URI} and loads it in the {@link ResourceSet} of the
     * {@link TransactionalEditingDomain} if is not already loaded
     * 
     * @param uri the uri where the resource can be found
     * @param editingDomain the current transactional editing domain, in which the resource should be loaded
     * @return the main EPackage of the metamodel at the URI
     */
    public static EPackage getMainPackageByURI(URI uri, TransactionalEditingDomain editingDomain) {
        ResourceSet resourceSet = editingDomain.getResourceSet();

        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
                new EcoreResourceFactoryImpl());

        Resource metamodelResource = resourceSet.getResource(uri, true);

        if (metamodelResource == null) {
            System.out.println("Could not load resource " + uri.toString());
            return null;
        }

        EPackage metamodel = (EPackage) metamodelResource.getContents().get(0);

        return metamodel;
    }
    
    /**
     * Creates a {@link Metamodel} in the given layer and with the given main package 
     * @param mainPackage the main package of the metamodel
     * @param layer the layer of the metamodel inside the {@link ModularReferenceStructure}
     * @return the just created metamodel
     */
    public static Metamodel createMetamodel(EPackage mainPackage, Layer layer) {
        Metamodel metamodel = MrsFactory.eINSTANCE.createMetamodel();
        metamodel.setMainPackage(mainPackage);
        metamodel.setLayer(layer);
        metamodel.setName(mainPackage.getName());
        return metamodel;
        
    }
    
    /**
     * Opens a {@link LoadResourceFromWorkspaceDialog} and returns the resulting list of URIs
     * @param shell The {@link Shell} in which the dialog will be created
     * @return the list of the URIs of the resources selected by the user
     */
    public static List<URI> openLoadResourceFromWorkspaceDialog(Shell shell) {
        LoadResourceFromWorkspaceDialog dialog = new LoadResourceFromWorkspaceDialog(shell);
        dialog.open();
        
        return ((dialog.getURIText() == null) || (dialog.getURIText().isEmpty())) ? null : dialog.getURIs();
    }
    
    /**
     * Returns the first {@link Metamodel}  found in the metamodels collection, whose main package is mainPackage
     * @param mainPackage the main package
     * @param metamodels the collection of metamodels to be looked into
     * @return the metamodel if found, null if not
     */
    public static Metamodel getCorrespondingMetamodel(EPackage mainPackage, Collection<Metamodel> metamodels) {
        for (Metamodel m : metamodels) {
            if (m.getMainPackage() == mainPackage)
                return m;
        }
        return null;
    }
    
    public static boolean metamodelAlreadyExists(EPackage mainPackage, ModularReferenceStructure mrs) {
		for (Layer layer : mrs.getLayers()) {
			for (Metamodel metamodel : layer.getMetamodels()) {
				if (mainPackage.equals(metamodel.getMainPackage()))
					return true;
			}
		}
		return false;
	}
    
    /**
     * Returns all metamodels present in the ModularReferenceStructure
     * 
     * @param mrs
     *            the ModularReferenceStructure
     * @return a Collection containing the metamodels
     */
    public static Collection<Metamodel> getAllMetamodels(ModularReferenceStructure mrs) {
        Collection<Metamodel> result = new ArrayList<Metamodel>();
        for (Layer l : mrs.getLayers()) {
            result.addAll(l.getMetamodels());
        }
        return result;
    }
    
    /**
     * Returns the main EPackage of the metamodel containing ePackage
     * 
     * @param ePackage
     * @return ePackage if it has no super package, otherwise the top most package of the super
     *         package of ePackage
     */
    public static EPackage getTopMostPackage(EPackage ePackage) {
        if (ePackage.getESuperPackage() == null)
            return ePackage;
        else
            return getTopMostPackage(ePackage.getESuperPackage());
    }

}
