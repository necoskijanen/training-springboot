# Git Commit Rules

## 1. Commit Message Rules
- Commit messages should be written in **English** (Japanese is allowed in exceptional cases).
- Format: `<type>(<scope>): <subject>`
  - `feat`: Add new feature
  - `fix`: Bug fix
  - `docs`: Documentation changes
  - `style`: Code formatting (no effect on behavior)
  - `refactor`: Refactoring (no effect on behavior)
  - `test`: Add or modify tests
  - `chore`: Changes related to build, dependencies, etc.
- Example:
  ```sh
  feat(api): add user authentication feature
  fix(ui): resolve button alignment issue
  ```


## 2. Commit Granularity
- Each commit should represent **one logical change**.
- Large changes should be **split into smaller commits**.
- Check changes before committing using `git diff`.

## 3. Branching Strategy
- Direct commits to `main` / `develop` branches are prohibited.
- Branch naming convention: `<type>/<issue-number>-<short-description>`
- Example: `feature/123-login-ui`, `fix/456-api-error`

## 4. Code Review
- Create a pull request (PR).
- Clearly state the title and description of the PR.
- Link related issues if necessary.

## 5. Creating Pull Requests with `gh`
- Use `gh pr create` to create a PR.
- Example:
  ```sh
  gh pr create --base develop --head feature/123-login-ui --title "Add login UI" --body "This PR adds a new login UI."
  ```
- Checklist when creating a PR:
  - Have you specified the appropriate base branch?
  - Is the title concise and clear?
  - Does the body describe the background and details of the change?

## 6. Merging Pull Requests with `gh`
- Use `gh pr merge` to merge a PR.
- Choose an option based on the merge type:
  - `--merge` (default): Creates a regular merge commit.
  - `--squash`: Squashes commits into one before merging.
  - `--rebase`: Rebase and merge.
  - Example:
    ```sh
    gh pr merge --squash --delete-branch
    ```
- Checklist when merging a PR:
  - Ensure necessary tests have passed.
  - Confirm related issues are closed.
  - Delete unnecessary branches (`--delete-branch`).

## 7. Miscellaneous
- Do not commit unnecessary files (e.g., `node_modules`).
- Use `git rebase` to tidy up the history.
- Use `git commit --amend` to amend the previous commit.
