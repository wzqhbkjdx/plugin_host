package cent.news.com.baseframe.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import cent.news.com.baseframe.modules.log.L;
import okhttp3.ResponseBody;

public class BaseGsonUtils {

    public static final Object readBody(Gson gson, Charset charset, ResponseBody body, Type type) throws IOException {
        if (body.contentType() != null) {
            charset = body.contentType().charset(charset);
        }

        L.tag("GsonConverter");

        InputStream is = body.byteStream();

        InputStreamReader inputStreamReader = new InputStreamReader(is, charset);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder result = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line + "\n");
            }
            String json = result.toString();
            L.tag("SKY-HTTP-RETURN");
            L.i(result.toString());
            return gson.fromJson(json, type);
        } finally {
            try {
                inputStreamReader.close();
                is.close();
                bufferedReader.close();

            } catch (IOException ignored) {
            }
        }
    }

    public static class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringAdapter();
        }
    }
    private static class StringAdapter extends TypeAdapter<String> {
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }
        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null|| value.equals("")) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }


}
