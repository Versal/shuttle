# Integration Testing with Clojure and Clojurescript

A popular framework for integration testing web apps is Selenium, which programmatically controls a browser to run tests. Selenium has its drawbacks, namely tests can be [slow, brittle and non-deterministic](http://jdrew33.blogspot.com/2012/02/pros-and-cons-of-selenium.html). In this post, we'll look at an alternative integration testing solution that uses Clojure and Clojurescript.

The ideal way to do integration testing would be to write tests that can make frontend calls and then verify state changes on the backend. We thought that Clojure (a modern dialect of Lisp that runs on the JVM) and ClojureScript (its Javascript variant) would be good candidates for the test framework since it allows us to use essentially the same language on both the frontend and backend. An added benefit is that the backend can be written in any language that runs on the JVM, and the client code can be Javascript or anything that compiles to Javascript. As an example, Versal's stack is primarily Scala and Coffeescript.

To allow communication between the frontend and the backend, we'll use the excellent utility [cemerick/yonder](http://github.com/cemerick/yonder). Yonder allows the server to remotely run ClojureScript through an nREPL (network REPL) connection, and includes helper functions for setting up and managing the nREPL connection. When a client first loads the page, it opens a connection to server and then waits for commands.

```Clojure
(repl/connect "http://localhost:9000/repl")
```

The test suite must create an nREPL endpoint on the server side before it loads the client. Yonder's mechanism for doing this is `open-session`, which returns a session handle for the REPL it creates. `open-session` takes two parameters, `:prepare` (function that sets up the client), and `:new-server` (a description for the server that acts as the nREPL endpoint). For the prepare function, we'll use the default function included with yonder that loads the client page using PhantomJS. For the server, we'll use the default nREPL server with an additional middleware that allows ClojureScript evaluation.

```Clojure
(yonder/open-session
              {:prepare yonder/prepare-phantomjs
               :new-server
               {:handler (server/default-handler
                           #'cemerick.piggieback/wrap-cljs-repl)}})
```

With the session returned by `open-session`, we can now execute ClojureScript on the client remotely from our Clojure server!

```Clojure
(yonder/eval session (+ 1 2 3))
(yonder/eval session (into [] (js/Array :a 'b ::c "d")))
```

Now we're ready to write our integration tests. For a sample test, let's make the client perform an Ajax POST and store the submission on the server. Then we'll verify that we received the correct submission.

Ajax request with some sample data:
```Clojure
(defn ^:export submit []
  (xhr [:post "/api/submit"] {:submission 42}
       (fn [_] (log "Finished submission"))))
```

Compojure route on the server that sets an atom to the value it receives.
```Clojure
(POST "/api/submit"
                 {{s :submission} :params}
                 ;; Modify server state
                 (reset! submission s))
```

So our final test looks like this. Note that the prepare function opens up test.html instead of the default index.html. After we have an nREPL session, we call invoke the ajax call on the client side, wait a few seconds for the request to complete, and verify the result in the atom.
```Clojure
(deftest browser-test
         (yonder/with-session
           [session
            (yonder/open-session
              {:prepare (partial yonder/prepare-phantomjs
                                 {:url "http://localhost:8080/test.html"})
               :new-server
               {:handler (server/default-handler
                           #'cemerick.piggieback/wrap-cljs-repl)}})]
           (yonder/eval session (cljs.test/submit))
           (Thread/sleep 4000)
           (is (= "42" @submission))))
```

We can run our test from the command line with `lein test`.

```bash
wei:shuttle wei$ lein test

lein test shuttle.core-test
2013-03-06 13:46:34.936:INFO:oejs.Server:jetty-7.6.1.v20120215

Testing shuttle.core-test
2013-03-06 13:46:35.142:INFO:oejs.AbstractConnector:Started SelectChannelConnector@0.0.0.0:8080

Ran 1 tests containing 1 assertions.
0 failures, 0 errors.
```

Please check out the project at [versal/shuttle](http://www.github.com/versal/shuttle)!
