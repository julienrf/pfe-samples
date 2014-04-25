package controllers;

import play.api.http.MediaRange;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;
import java.util.function.Supplier;

public class Render extends Controller {

    public static Result render(Version... versions) {
        List<MediaRange> acceptedTypes = request().acceptedTypes();
        if (acceptedTypes.isEmpty() && versions.length > 0) {
            return versions[0].resultThunk.get();
        }
        for (MediaRange mediaRange : acceptedTypes) {
            for (Version version : versions) {
                if (mediaRange.accepts(version.mimeType)) {
                    return version.resultThunk.get();
                }
            }
        }
        return status(NOT_ACCEPTABLE);
    }

    public static class Version {
        public final String mimeType;
        public final Supplier<Result> resultThunk;

        public Version(String mimeType, Supplier<Result> resultThunk) {
            this.mimeType = mimeType;
            this.resultThunk = resultThunk;
        }
    }

    public static Version version(String mimeType, Supplier<Result> resultThunk) {
        return new Version(mimeType, resultThunk);
    }

}