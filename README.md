# sv

A simple Clojure app to do two things:

- Parse structured text files with an expected structure into a collection of records,
  then display those records sorted in one of three ways
- Expose a web API to create and browse an (in-memory) collection of such records

## Usage

This app can be run in either of two ways.

### Command-line utility

Use the `cli` profile to run the command line interface version of the app,
which will show a tabular view of records:

```
$ lein cli -- resources/test.csv resources/test.psv resources/test.ssv --sort-by birthdate --ignore-errors

| :last-name | :first-name |   :gender | :favorite-color | :date-of-birth |
|------------+-------------+-----------+-----------------+----------------|
|   Blashton |        Neil |      male |         crimson |     08/16/1984 |
|     Ashton |        Neil | nonbinary |          indigo |     08/17/1984 |
|   Drashton |        Neil |    female |         crimson |     08/18/1984 |
|   Smashton |        Neil | nonbinary |          indigo |     08/19/1984 |
|  mBrashton |        Neil |      male |         crimson |     08/20/1984 |
|    Yashton |        Neil |    female |          indigo |     08/21/1984 |
```

If the `--ignore-errors` flag is not included and the files provided are malformed,
the app will refuse to proceed. Valid data meets the following criteria:

- The file extension is one of .csv, .psv, or .ssv
- A file with a given extension contains five pieces of data per line, separated
  by the appropriate separator: "," for .csv, "|" for .psv, and " " for .csv
- The fields are in the order: last name, first name, gender, favorite color, birthday
- Gender is one of "male", "female", "nonbinary", or "other".
- Birthday is in the format MM/dd/yyyy (e.g. "08/16/1984")

To see a list of available options, including sort modes, run `lein cli -- --help`.

### Web API

Use the `api` profile to run the API version of the app, which will expose
a simple endpoint to access and add to a set of records (with a default
port of 3000):

```
$ lein api -- --port 8080
```

To see the available records:

```
$ curl http://localhost:3000/records/gender
$ curl http://localhost:3000/records/birthdate
$ curl http://localhost:3000/records/name
```

To add a record, POST a line of plain text in the formats described in
the "Command-line utility" section of this README to the `/records/` endpoint.
If successful, the resulting entity will be echoed back at you:

```
$ curl -X POST -H "Content-Type: text/plain" --data "West,Iris,female,blue,02/23/1976" http://localhost:3000/records/
$ curl -X POST -H "Content-Type: text/plain" --data "West|Wally|male|yellow|02/23/1997" http://localhost:3000/records/
$ curl -X POST -H "Content-Type: text/plain" --data "Allen Barry male red 05/13/1978" http://localhost:3000/records/
```

If there are problems with the input (see the previous section for possible issues),
"Errors in input" will be echoed back:

```
$ curl -X POST -H "Content-Type: text/plain" --data "Thawne Eobard male green 05-12-2478" http://localhost:3000/records/
Errors in input
```

## Coverage

This project uses [Cloverage](https://github.com/cloverage/cloverage) to gauge test
coverage. To run all tests and see the coverage report, run `lein cloverage`.

However, given that the project divides into a functional core and an
application-logic shell, it may be more useful to look exclusively at the
coverage for the former by running `lein cloverage -e "sv\.cmd\..+"`.

## License

Copyright © 2020 Neil Ashton

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
