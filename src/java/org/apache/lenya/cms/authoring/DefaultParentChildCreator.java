package org.wyona.cms.authoring;

import java.io.File;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner
 */
public class DefaultParentChildCreator extends AbstractParentChildCreator {
    static Category log = Category.getInstance(DefaultParentChildCreator.class);

    /**
     * Return the child type.
     *
     * @param childType a <code>short</code> value
     * @return a <code>short</code> value
     * @exception Exception if an error occurs
     */
    public short getChildType(short childType) throws Exception {
	return childType;
    }

    /**
     * Generate a three id.
     *
     * @param childId a <code>String</code> value
     * @param childType a <code>short</code> value
     * @return a <code>String</code> value
     * @exception Exception if an error occurs
     */
    public String generateTreeId(String childId, short childType)
	throws Exception {
	return childId;
    }

    /**
     * Default implementation for creation.
     *
     * @param samplesDir a <code>File</code> value
     * @param parentDir a <code>File</code> value
     * @param childId a <code>String</code> value
     * @param childType a <code>short</code> value
     * @exception Exception if an error occurs
     */
    public void create(File samplesDir, File parentDir,
		       String childId, short childType,String childName)
	throws Exception {

	log.warn("DefaultParentChildCreator.create() has been called.");
    }
}
