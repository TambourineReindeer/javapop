package com.novusradix.JavaPop.Client;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Loads and keeps models in a map given a URL.
 * @author gef
 */
public class ModelFactory {

    HashMap<ModelKey, Model> models;

    public ModelFactory() {
        models = new HashMap<ModelKey, Model>();
    }

    public Model getModel(URL model, URL texture) throws IOException {
        ModelKey k = new ModelKey(model, texture);
        if (models.containsKey(k)) {
            return models.get(k);
        }

        Model m = new Model(ModelData.fromURL(model), texture);
        models.put(k, m);
        return m;

    }

    private class ModelKey {

        private URL model,  texture;

        public ModelKey(URL m, URL t) {
            model = m;
            texture = t;
        }

        @Override
        public boolean equals(Object o) {
            if (!o.getClass().equals(ModelKey.class)) {
                return false;
            }
            ModelKey m2 = (ModelKey) o;
            return m2.model == model && m2.texture == texture;
        }

        @Override
        public int hashCode() {
            return (model==null?0:model.hashCode()^1) ^ (texture==null?0:texture.hashCode());
        }
    }
}
