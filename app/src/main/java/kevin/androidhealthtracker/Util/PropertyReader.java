package kevin.androidhealthtracker.Util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
    private Context context;
    private Properties properties;
    public PropertyReader(Context context, Properties properties) {
        this.context = context;
        this.properties = properties;
    }

    public Properties getProperties(String fileName) throws IOException {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            properties.load(inputStream);

        }catch (Exception e ){
            System.out.println(e.getMessage());
        }
        return properties;
    }
}
