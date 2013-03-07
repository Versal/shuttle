# Shuttle

Shuttle is an integration testing helper that can make frontend/JS calls and verify state changes on the backend/JVM.

## Usage

`lein test` will run all the tests. Currently there's only one test that verifies an Ajax submission.

## How it works

The test runner uses [cemerick/yonder](https://github.com/cemerick/yonder) to initialize a [nREPL](https://github.com/clojure/tools.nrepl) and upgrade it to a Clojurescript repl. Yonder opens the browser to `resources/private/html/test.html`. The page connects to the waiting REPL on port 9000, and is now ready to eval Clojurescript.

[More info](https://github.com/Versal/shuttle/blob/master/post.md)

## Extending

Add frontend tests to `src/cljs/test.cljs`

Add backend tests to `test/shuttle/core_test.clj`.
