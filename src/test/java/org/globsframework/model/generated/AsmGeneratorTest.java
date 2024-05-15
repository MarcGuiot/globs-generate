package org.globsframework.model.generated;

import org.globsframework.metamodel.fields.Field;
import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.GlobTypeBuilder;
import org.globsframework.metamodel.GlobTypeBuilderFactory;
import org.globsframework.metamodel.annotations.AutoIncrementAnnotationType;
import org.globsframework.metamodel.annotations.DefaultBooleanAnnotationType;
import org.globsframework.metamodel.fields.*;
import org.globsframework.model.MutableGlob;
import org.globsframework.model.globaccessor.get.GlobGetDoubleAccessor;
import org.globsframework.model.globaccessor.get.GlobGetIntAccessor;
import org.globsframework.model.globaccessor.get.GlobGetLongAccessor;
import org.globsframework.model.globaccessor.set.GlobSetDoubleAccessor;
import org.globsframework.model.globaccessor.set.GlobSetIntAccessor;
import org.globsframework.model.globaccessor.set.GlobSetLongAccessor;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AsmGeneratorTest {

    @Test
    public void globGen() {

        System.setProperty("org.globsframework.builder", "org.globsframework.model.generator.GeneratorGlobFactoryService");
        GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("GlobType1");

        IntegerField i1 = globTypeBuilder.declareIntegerField("int");
        DoubleField d1 = globTypeBuilder.declareDoubleField("my double");
        LongField l1 = globTypeBuilder.declareLongField("my long");
        LongArrayField la1 = globTypeBuilder.declareLongArrayField("an array of Long");
        GlobType globType = globTypeBuilder.get();

        MutableGlob instantiate = globType.instantiate();

        Assert.assertFalse(instantiate.isSet(i1));
        Assert.assertFalse(instantiate.isSet(d1));
        Assert.assertFalse(instantiate.isSet(l1));
        Assert.assertFalse(instantiate.isSet(la1));

        Assert.assertTrue(instantiate.isNull(i1));
        Assert.assertTrue(instantiate.isNull(d1));
        Assert.assertTrue(instantiate.isNull(l1));
        Assert.assertTrue(instantiate.isNull(la1));

        Assert.assertNull(instantiate.get(i1));
        Assert.assertNull(instantiate.get(d1));
        Assert.assertNull(instantiate.get(l1));
        Assert.assertNull(instantiate.get(la1));

        instantiate.set(i1, 2);
        Assert.assertNotNull(instantiate.get(i1));
        Assert.assertTrue(instantiate.isSet(i1));
        Assert.assertFalse(instantiate.isSet(d1));
        Assert.assertFalse(instantiate.isSet(l1));
        Assert.assertFalse(instantiate.isSet(la1));

        instantiate.set(d1, 2.2);
        Assert.assertTrue(instantiate.isSet(d1));
        Assert.assertFalse(instantiate.isSet(l1));
        Assert.assertFalse(instantiate.isSet(la1));
        instantiate.set(l1, 123);

        Assert.assertTrue(instantiate.isSet(l1));
        Assert.assertFalse(instantiate.isSet(la1));

        instantiate.set(la1, new long[]{2,3});
        Assert.assertTrue(instantiate.isSet(la1));
        Assert.assertTrue(instantiate.isSet(l1));
        Assert.assertTrue(instantiate.isSet(i1));


        Assert.assertEquals(2, instantiate.get(i1).intValue());
        Assert.assertEquals(2.2, instantiate.get(d1), 0.01);
        Assert.assertEquals(123, instantiate.get(l1).longValue());
        Assert.assertEquals(2, instantiate.get(la1)[0]);
        Assert.assertEquals(3, instantiate.get(la1)[1]);

        GlobGetIntAccessor iGet = globType.getGlobFactory().getGetAccessor(i1);
        GlobGetDoubleAccessor dGet = globType.getGlobFactory().getGetAccessor(d1);
        GlobGetLongAccessor lGet = globType.getGlobFactory().getGetAccessor(l1);

        GlobSetIntAccessor iSet = globType.getGlobFactory().getSetAccessor(i1);
        GlobSetDoubleAccessor dSet = globType.getGlobFactory().getSetAccessor(d1);
        GlobSetLongAccessor lSet = globType.getGlobFactory().getSetAccessor(l1);

        iSet.set(instantiate, 3);
        dSet.set(instantiate, 3.3);
        lSet.set(instantiate, 321L);

        Assert.assertEquals(3, iGet.getNative(instantiate));
        Assert.assertEquals(3.3, dGet.getNative(instantiate), 0.001);
        Assert.assertEquals(321L, lGet.getNative(instantiate));


        Assert.assertEquals(3, iGet.get(instantiate).intValue());
        Assert.assertEquals(3.3, dGet.get(instantiate), 0.001);
        Assert.assertEquals(321L, lGet.get(instantiate).longValue());

        instantiate.safeApply((field, value) -> System.out.println(field.getName() + ":" + value));
        instantiate.safeAccept(new FieldValueVisitor.AbstractFieldValueVisitor(){
            public void notManaged(Field field, Object value) throws Exception {
                System.out.println(field.getName() + " : " + value);
            }
        });

        iSet.set(instantiate, null);
        Assert.assertNotNull(instantiate.get(d1));
        Assert.assertNotNull(instantiate.get(l1));
        dSet.set(instantiate, null);
        Assert.assertNotNull(instantiate.get(l1));
        lSet.set(instantiate, null);

        Assert.assertNull(instantiate.get(i1));
        Assert.assertNull(instantiate.get(d1));
        Assert.assertNull(instantiate.get(l1));

        Assert.assertTrue(instantiate.isSet(i1));
        Assert.assertTrue(instantiate.isSet(d1));
        Assert.assertTrue(instantiate.isSet(l1));
        Assert.assertTrue(instantiate.isSet(la1));

        MutableGlob duplicate = instantiate.duplicate();

        Assert.assertTrue(instantiate.matches(duplicate));
        Assert.assertNotSame(instantiate, duplicate);

        instantiate.unset(d1);
        Assert.assertFalse(instantiate.isSet(d1));
        instantiate.unset(i1);
        Assert.assertFalse(instantiate.isSet(i1));
        instantiate.unset(l1);
        Assert.assertFalse(instantiate.isSet(l1));
        instantiate.unset(la1);
        Assert.assertFalse(instantiate.isSet(la1));
    }

    @Test
    public void testAnnotations() {
        System.setProperty("org.globsframework.builder", "org.globsframework.model.generator.GeneratorGlobFactoryService");
        DefaultBooleanAnnotationType.TYPE.instantiate().set(DefaultBooleanAnnotationType.DEFAULT_VALUE, true);
        final MutableGlob instantiate = AutoIncrementAnnotationType.TYPE.instantiate();
        Assert.assertEquals(instantiate.getKey(), AutoIncrementAnnotationType.KEY);
        Assert.assertSame(instantiate.getType(), AutoIncrementAnnotationType.TYPE);
    }

    @Test
    public void GlobWithMoreThan32Field() {
        System.setProperty("org.globsframework.builder", "org.globsframework.model.generator.GeneratorGlobFactoryService");
        GlobTypeBuilder globTypeBuilder = GlobTypeBuilderFactory.create("GlobType1");

        List<Field> allField = new ArrayList<>();
        for (int i = 0; i < 75; i++) {
            allField.add(globTypeBuilder.declareIntegerField("int" + i));
            allField.add(globTypeBuilder.declareDoubleField("my double" + i));
            allField.add(globTypeBuilder.declareStringField("my String" + i));
        }
        GlobType globType = globTypeBuilder.get();
        MutableGlob instantiate = globType.instantiate();
        for (Field field : allField) {
            Assert.assertFalse(instantiate.isSet(field));
        }
        for (Field field : allField) {
            Assert.assertTrue(instantiate.isNull(field));
            if (field.getName().contains("int")){
                instantiate.setValue(field, 0);
            } else if (field.getName().contains("double")) {
                instantiate.setValue(field, 0.0);
            } else if (field.getName().contains("String")) {
                instantiate.setValue(field, "STR");
            }
            Assert.assertTrue(instantiate.isSet(field));
            Assert.assertFalse(instantiate.isNull(field));
        }
        for (Field field : allField) {
            Assert.assertTrue(instantiate.isSet(field));
            instantiate.unset(field);
            Assert.assertFalse(instantiate.isSet(field));
        }
        for (Field field : allField) {
            Assert.assertFalse(instantiate.isSet(field));
            Assert.assertTrue(instantiate.isNull(field));
            instantiate.setValue(field, null);
            Assert.assertTrue(instantiate.isNull(field));
            Assert.assertTrue(instantiate.isSet(field));
        }
    }
}
