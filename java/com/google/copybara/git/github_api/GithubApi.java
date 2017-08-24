/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.copybara.git.github_api;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.copybara.RepoException;
import com.google.copybara.ValidationException;
import com.google.copybara.profiler.Profiler;
import com.google.copybara.profiler.Profiler.ProfilerTask;
import java.util.List;

/**
 * A mini API for getting and updating GitHub projects through the GitHub REST API.
 */
public class GithubApi {

  private final GitHubApiTransport transport;
  private final Profiler profiler;

  public GithubApi(GitHubApiTransport transport, Profiler profiler) {
    this.transport = Preconditions.checkNotNull(transport);
    this.profiler = Preconditions.checkNotNull(profiler);
  }

  /**
   * Get all the pull requests for a project
   * @param projectId a project in the form of "google/copybara"
   */
  public ImmutableList<PullRequest> getPullRequests(String projectId)
      throws RepoException, ValidationException {
    try (ProfilerTask ignore = profiler.start("github_api/pulls")) {
      List<PullRequest> result =
          transport.get(String.format("repos/%s/pulls", projectId),
              new TypeToken<List<PullRequest>>() {
              }.getType());

      return ImmutableList.copyOf(result);
    }
  }

  /**
   * Get a specific pull request for a project
   * @param projectId a project in the form of "google/copybara"
   * @param number the issue number
   */
  public PullRequest getPullRequest(String projectId, long number)
      throws RepoException, ValidationException {
    try (ProfilerTask ignore = profiler.start("github_api/pulls/NUMBER")) {
      return transport.get(
          String.format("repos/%s/pulls/%d", projectId, number), PullRequest.class);
    }
  }
  /**
   * Get a specific issue for a project.
   *
   * <p>Use this method to get the Pull Request labels.
   * @param projectId a project in the form of "google/copybara"
   * @param number the issue number
   */
  public Issue getIssue(String projectId, long number)
      throws RepoException, ValidationException {
    try (ProfilerTask ignore = profiler.start("github_api/issues/NUMBER")) {
      return transport.get(String.format("repos/%s/issues/%d", projectId, number), Issue.class);
    }
  }
}