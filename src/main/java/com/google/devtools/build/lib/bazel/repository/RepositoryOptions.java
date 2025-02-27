// Copyright 2016 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.bazel.repository;

import com.google.auto.value.AutoValue;
import com.google.devtools.build.lib.cmdline.LabelSyntaxException;
import com.google.devtools.build.lib.cmdline.RepositoryName;
import com.google.devtools.build.lib.util.OptionsUtils;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.common.options.Converter;
import com.google.devtools.common.options.EnumConverter;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionDocumentationCategory;
import com.google.devtools.common.options.OptionEffectTag;
import com.google.devtools.common.options.OptionMetadataTag;
import com.google.devtools.common.options.OptionsBase;
import com.google.devtools.common.options.OptionsParsingException;
import java.util.List;

/** Command-line options for repositories. */
public class RepositoryOptions extends OptionsBase {

  @Option(
      name = "repository_cache",
      oldName = "experimental_repository_cache",
      defaultValue = "null",
      documentationCategory = OptionDocumentationCategory.BAZEL_CLIENT_OPTIONS,
      effectTags = {OptionEffectTag.BAZEL_INTERNAL_CONFIGURATION},
      converter = OptionsUtils.PathFragmentConverter.class,
      help =
          "Specifies the cache location of the downloaded values obtained "
              + "during the fetching of external repositories. An empty string "
              + "as argument requests the cache to be disabled.")
  public PathFragment experimentalRepositoryCache;

  @Option(
      name = "registry",
      defaultValue = "null",
      allowMultiple = true,
      documentationCategory = OptionDocumentationCategory.BAZEL_CLIENT_OPTIONS,
      effectTags = {OptionEffectTag.CHANGES_INPUTS},
      help =
          "Specifies the registries to use to locate Bazel module dependencies. The order is"
              + " important: modules will be looked up in earlier registries first, and only fall"
              + " back to later registries when they're missing from the earlier ones.")
  public List<String> registries;

  @Option(
      name = "experimental_repository_cache_hardlinks",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.BAZEL_CLIENT_OPTIONS,
      effectTags = {OptionEffectTag.BAZEL_INTERNAL_CONFIGURATION},
      help =
          "If set, the repository cache will hardlink the file in case of a"
              + " cache hit, rather than copying. This is intended to save disk space.")
  public boolean useHardlinks;

  @Option(
      name = "experimental_repository_disable_download",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.BAZEL_CLIENT_OPTIONS,
      effectTags = {OptionEffectTag.UNKNOWN},
      metadataTags = {OptionMetadataTag.EXPERIMENTAL},
      help = "If set, downloading external repositories is not allowed.")
  public boolean disableDownload;

  @Option(
      name = "experimental_repository_downloader_retries",
      defaultValue = "0",
      documentationCategory = OptionDocumentationCategory.BAZEL_CLIENT_OPTIONS,
      effectTags = {OptionEffectTag.UNKNOWN},
      metadataTags = {OptionMetadataTag.EXPERIMENTAL},
      help =
          "The maximum number of attempts to retry a download error. If set to 0, retries are"
              + " disabled.")
  public int repositoryDownloaderRetries;

  @Option(
      name = "distdir",
      oldName = "experimental_distdir",
      defaultValue = "null",
      allowMultiple = true,
      documentationCategory = OptionDocumentationCategory.BAZEL_CLIENT_OPTIONS,
      effectTags = {OptionEffectTag.BAZEL_INTERNAL_CONFIGURATION},
      converter = OptionsUtils.PathFragmentConverter.class,
      help =
          "Additional places to search for archives before accessing the network "
              + "to download them.")
  public List<PathFragment> experimentalDistdir;

  @Option(
      name = "http_timeout_scaling",
      defaultValue = "1.0",
      documentationCategory = OptionDocumentationCategory.BAZEL_CLIENT_OPTIONS,
      effectTags = {OptionEffectTag.BAZEL_INTERNAL_CONFIGURATION},
      help = "Scale all timeouts related to http downloads by the given factor")
  public double httpTimeoutScaling;

  @Option(
      name = "override_repository",
      defaultValue = "null",
      allowMultiple = true,
      converter = RepositoryOverrideConverter.class,
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Overrides a repository with a local directory.")
  public List<RepositoryOverride> repositoryOverrides;

  @Option(
      name = "override_module",
      defaultValue = "null",
      allowMultiple = true,
      converter = ModuleOverrideConverter.class,
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Overrides a module with a local directory.")
  public List<ModuleOverride> moduleOverrides;

  @Option(
      name = "experimental_scale_timeouts",
      defaultValue = "1.0",
      documentationCategory = OptionDocumentationCategory.BAZEL_CLIENT_OPTIONS,
      effectTags = {OptionEffectTag.BAZEL_INTERNAL_CONFIGURATION},
      metadataTags = {OptionMetadataTag.EXPERIMENTAL},
      help =
          "Scale all timeouts in Starlark repository rules by this factor."
              + " In this way, external repositories can be made working on machines"
              + " that are slower than the rule author expected, without changing the"
              + " source code")
  public double experimentalScaleTimeouts;

  @Option(
      name = "experimental_repository_hash_file",
      defaultValue = "",
      documentationCategory = OptionDocumentationCategory.INPUT_STRICTNESS,
      effectTags = {OptionEffectTag.AFFECTS_OUTPUTS},
      metadataTags = {OptionMetadataTag.EXPERIMENTAL},
      help =
          "If non-empty, specifies a file containing a resolved value, against which"
              + " the repository directory hashes should be verified")
  public String repositoryHashFile;

  @Option(
      name = "experimental_verify_repository_rules",
      allowMultiple = true,
      defaultValue = "null",
      documentationCategory = OptionDocumentationCategory.INPUT_STRICTNESS,
      effectTags = {OptionEffectTag.AFFECTS_OUTPUTS},
      metadataTags = {OptionMetadataTag.EXPERIMENTAL},
      help =
          "If list of repository rules for which the hash of the output directory should be"
              + " verified, provided a file is specified by"
              + " --experimental_repository_hash_file.")
  public List<String> experimentalVerifyRepositoryRules;

  @Option(
      name = "experimental_resolved_file_instead_of_workspace",
      defaultValue = "",
      documentationCategory = OptionDocumentationCategory.GENERIC_INPUTS,
      effectTags = {OptionEffectTag.CHANGES_INPUTS},
      help = "If non-empty read the specified resolved file instead of the WORKSPACE file")
  public String experimentalResolvedFileInsteadOfWorkspace;

  @Option(
      name = "experimental_downloader_config",
      defaultValue = "null",
      documentationCategory = OptionDocumentationCategory.REMOTE,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Specify a file to configure the remote downloader with. This file consists of lines, "
              + "each of which starts with a directive (`allow`, `block` or `rewrite`) followed "
              + "by either a host name (for `allow` and `block`) or two patterns, one to match "
              + "against, and one to use as a substitute URL, with back-references starting from "
              + "`$1`. It is possible for multiple `rewrite` directives for the same URL to be "
              + "give, and in this case multiple URLs will be returned.")
  public String downloaderConfig;

  @Option(
      name = "ignore_dev_dependency",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.BAZEL_CLIENT_OPTIONS,
      effectTags = {OptionEffectTag.LOADING_AND_ANALYSIS},
      help =
          "If true, Bazel ignores `bazel_dep` and `use_extension` declared as `dev_dependency` in "
              + "the MODULE.bazel of the root module. Note that, those dev dependencies are always "
              + "ignored in the MODULE.bazel if it's not the root module regardless of the value "
              + "of this flag.")
  public boolean ignoreDevDependency;

  @Option(
      name = "check_direct_dependencies",
      defaultValue = "warning",
      converter = CheckDirectDepsMode.Converter.class,
      documentationCategory = OptionDocumentationCategory.BAZEL_CLIENT_OPTIONS,
      effectTags = {OptionEffectTag.LOADING_AND_ANALYSIS},
      help =
          "Check if the direct `bazel_dep` dependencies declared in the root module are the same"
              + " versions you get in the resolved dependency graph. Valid values are `off` to"
              + " disable the check, `warning` to print a warning when mismatch detected or `error`"
              + " to escalate it to a resolution failure.")
  public CheckDirectDepsMode checkDirectDependencies;

  @Option(
      name = "experimental_repository_cache_urls_as_default_canonical_id",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.BAZEL_CLIENT_OPTIONS,
      effectTags = {OptionEffectTag.LOADING_AND_ANALYSIS},
      metadataTags = {OptionMetadataTag.EXPERIMENTAL},
      help =
          "If true, use a string derived from the URLs of repository downloads as the canonical_id "
              + "if not specified. This causes a change in the URLs to result in a redownload even "
              + "if the cache contains a download with the same hash. This can be used to verify "
              + "that URL changes don't result in broken repositories being masked by the cache.")
  public boolean urlsAsDefaultCanonicalId;

  @Option(
      name = "experimental_check_external_repository_files",
      defaultValue = "true",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Check for modifications to files in external repositories. Consider setting "
              + "this flag to false if you don't expect these files to change outside of bazel "
              + "since it will speed up subsequent runs as they won't have to check a "
              + "previous run's cache.")
  public boolean checkExternalRepositoryFiles;

  @Option(
      name = "check_bazel_compatibility",
      defaultValue = "error",
      converter = BazelCompatibilityMode.Converter.class,
      documentationCategory = OptionDocumentationCategory.BZLMOD,
      effectTags = {OptionEffectTag.LOADING_AND_ANALYSIS},
      help =
          "Check bazel version compatibility of Bazel modules. Valid values are `error` to escalate"
              + " it to a resolution failure, `off` to disable the check, or `warning` to print a"
              + " warning when mismatch detected.")
  public BazelCompatibilityMode bazelCompatibilityMode;

  /** An enum for specifying different modes for checking direct dependency accuracy. */
  public enum CheckDirectDepsMode {
    OFF, // Don't check direct dependency accuracy.
    WARNING, // Print warning when mismatch.
    ERROR; // Throw an error when mismatch.

    /** Converts to {@link CheckDirectDepsMode}. */
    public static class Converter extends EnumConverter<CheckDirectDepsMode> {
      public Converter() {
        super(CheckDirectDepsMode.class, "direct deps check mode");
      }
    }
  }
  /** An enum for specifying different modes for bazel compatibility check. */
  public enum BazelCompatibilityMode {
    ERROR, // Check and throw an error when mismatched.
    WARNING, // Print warning when mismatched.
    OFF; // Don't check bazel version compatibility.

    /** Converts to {@link BazelCompatibilityMode}. */
    public static class Converter extends EnumConverter<BazelCompatibilityMode> {
      public Converter() {
        super(BazelCompatibilityMode.class, "Bazel compatibility check mode");
      }
    }
  }

  /**
   * Converts from an equals-separated pair of strings into RepositoryName->PathFragment mapping.
   */
  public static class RepositoryOverrideConverter
      extends Converter.Contextless<RepositoryOverride> {

    @Override
    public RepositoryOverride convert(String input) throws OptionsParsingException {
      String[] pieces = input.split("=", 2);
      if (pieces.length != 2) {
        throw new OptionsParsingException(
            "Repository overrides must be of the form 'repository-name=path'", input);
      }
      OptionsUtils.AbsolutePathFragmentConverter absolutePathFragmentConverter =
          new OptionsUtils.AbsolutePathFragmentConverter();
      PathFragment path;
      try {
        path = absolutePathFragmentConverter.convert(pieces[1]);
      } catch (OptionsParsingException e) {
        throw new OptionsParsingException(
            "Repository override directory must be an absolute path", input, e);
      }
      try {
        return RepositoryOverride.create(RepositoryName.create(pieces[0]), path);
      } catch (LabelSyntaxException e) {
        throw new OptionsParsingException("Invalid repository name given to override", input, e);
      }
    }

    @Override
    public String getTypeDescription() {
      return "an equals-separated mapping of repository name to path";
    }
  }

  /** Converts from an equals-separated pair of strings into ModuleName->PathFragment mapping. */
  public static class ModuleOverrideConverter extends Converter.Contextless<ModuleOverride> {

    @Override
    public ModuleOverride convert(String input) throws OptionsParsingException {
      String[] pieces = input.split("=", 2);
      if (pieces.length != 2) {
        throw new OptionsParsingException(
            "Module overrides must be of the form 'module-name=path'", input);
      }

      if (!RepositoryName.VALID_MODULE_NAME.matcher(pieces[0]).matches()) {
        throw new OptionsParsingException(
            String.format(
                "invalid module name '%s': valid names must 1) only contain lowercase letters"
                    + " (a-z), digits (0-9), dots (.), hyphens (-), and underscores (_); 2) begin"
                    + " with a lowercase letter; 3) end with a lowercase letter or digit.",
                pieces[0]));
      }

      OptionsUtils.AbsolutePathFragmentConverter absolutePathFragmentConverter =
          new OptionsUtils.AbsolutePathFragmentConverter();
      try {
        var unused = absolutePathFragmentConverter.convert(pieces[1]);
      } catch (OptionsParsingException e) {
        throw new OptionsParsingException(
            "Module override directory must be an absolute path", input, e);
      }
      return ModuleOverride.create(pieces[0], pieces[1]);
    }

    @Override
    public String getTypeDescription() {
      return "an equals-separated mapping of module name to path";
    }
  }

  /** A repository override, represented by a name and an absolute path to a repository. */
  @AutoValue
  public abstract static class RepositoryOverride {

    private static RepositoryOverride create(RepositoryName repositoryName, PathFragment path) {
      return new AutoValue_RepositoryOptions_RepositoryOverride(repositoryName, path);
    }

    public abstract RepositoryName repositoryName();

    public abstract PathFragment path();
  }

  /** A module override, represented by a name and an absolute path to a module. */
  @AutoValue
  public abstract static class ModuleOverride {

    private static ModuleOverride create(String moduleName, String path) {
      return new AutoValue_RepositoryOptions_ModuleOverride(moduleName, path);
    }

    public abstract String moduleName();

    public abstract String path();
  }
}
