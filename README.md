AvroExamples
============
-------------------------------------------------------------------------------------------
References:


http://www.hascode.com/2014/03/using-apache-avro-with-java-and-maven/

http://avro.apache.org/docs/1.7.5/gettingstartedjava.html
--------------------------------------------------------------------------------------------
Asmath's fiinding

1##. pom.xml will show error in project perspective at Generate resources goal of the avro-maven-plugin . Ignore this error as it is existing issue with avro plugin.

<groupId>org.apache.avro</groupId>
	<artifactId>avro-maven-plugin</artifactId>
	<version>1.7.6</version>
	<executions>
	<execution>
	<phase>generate-sources</phase>
2##. book.avsc (Book avro schema) will generate the classes by running mvn generate-sources goal from the eclipse. 
Right click POM.XML file and run as Generate Sources from Maven. This will generate class Book in the package com.cts.avro.entity

"namespace": "com.cts.avro.entity",
	"type": "record",
	"name": "Book",
	
3##. Maven will store all the dependencies in local repository under /users/kmohamm2/.m2 .... Sometimes we need to create jar with
dependencies. This can be done by the maven shade plugin

<plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
    <version>1.4</version>
    <executions>
      <execution>
        <phase>package</phase>
        <goals>
        <goal>shade</goal>
        </goals>
        <configuration>
        <shadedArtifactAttached>true</shadedArtifactAttached>
        <shadedClassifierName>all</shadedClassifierName>
        </configuration>
      </execution>
    </executions>
      </plugin> 

################################# Entering Same content from the URL below ###############################################

Apache Avro is a serialization framework similar to Google’s Protocol Buffers or Apache Thrift and offering features like rich data structures, a compact binary format, simple integration with dynamic languages and more.

In the following short five minute tutorial, we’re going to specify a schema to serialize books in a JSON format, we’re using the Avro Maven plugin to generate the stub classes and finally we’re serializing the data into a single file.


Avro Schema Declaration
Avro Schema Declaration
Contents

Why another Framework
Maven Dependencies
Defining the Book Schema
Schema Compiling / Generating Classes
Serializing / Deserializing from a File
Tutorial Sources
Resources
 
Why another Framework

What are the advantages of Avro over Google Protocol Buffers (see my article about Protocol Buffers) or Apache Thrift?

Imho one thing I like is the use of JSON as a data format, another good thing is the fact that the schema is written to the serialized file so there might be less problems when using different versions of a schema.

If you’re interested in a more detailed (but biased) comparison, feel free to have a look at this nice presentation from Igor Anishchenko.

Maven Dependencies

We’re adding two dependencies to our pom.xml – the one is the Apache Avro library, the other one is the Maven plugin that allows us to generate Java classes from our format specifications.

We’re configuring the plugin to look in src/main/avro for specification files and to put the generated Java classes to src/main/java.

<dependencies>
	<dependency>
		<groupId>org.apache.avro</groupId>
		<artifactId>avro</artifactId>
		<version>1.7.6</version>
	</dependency>
</dependencies>
 
<build>
	<plugins>
		<plugin>
			<groupId>org.apache.avro</groupId>
			<artifactId>avro-maven-plugin</artifactId>
			<version>1.7.6</version>
			<executions>
				<execution>
					<phase>generate-sources</phase>
					<goals>
						<goal>schema</goal>
					</goals>
					<configuration>
						<sourceDirectory>${project.basedir}/src/main/avro/</sourceDirectory>
						<outputDirectory>${project.basedir}/src/main/java/</outputDirectory>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
Defining the Book Schema

An Avro schema is written in the JSON format and we may use different primitive or complex types here.

A detailed documentation can be found in the Avro documentation.

This is our book schema in src/main/avro/book.avsc:

{
	"namespace": "com.hascode.entity",
	"type": "record",
	"name": "Book",
	"fields": [
		{"name": "name", "type": "string"},
		{"name": "id",  "type": ["int", "null"]},
		{"name": "category", "type": ["string", "null"]}
	 ]
}
Schema Compiling / Generating Classes

You may run the following command to create the Book class needed from the schema file:

mvn generate-sources
Serializing / Deserializing from a File

The following snippet serializes books to a file and afterwards deserializes it and prints it to the output.

package com.hascode.tutorial;
 
import java.io.File;
import java.io.IOException;
 
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
 
import com.hascode.entity.Book;
 
public class FileSerializationExample {
	public static void main(final String[] args) throws IOException {
		Book book1 = Book.newBuilder().setId(123).setName("Programming is fun")
				.setCategory("Fiction").build();
		Book book2 = new Book("Some book", 456, "Horror");
		Book book3 = new Book();
		book3.setName("And another book");
		book3.setId(789);
		File store = File.createTempFile("book", ".avro");
 
		// serializing
		System.out
				.println("serializing books to temp file: " + store.getPath());
		DatumWriter<Book> bookDatumWriter = new SpecificDatumWriter<Book>(
				Book.class);
		DataFileWriter<Book> bookFileWriter = new DataFileWriter<Book>(
				bookDatumWriter);
		bookFileWriter.create(book1.getSchema(), store);
		bookFileWriter.append(book1);
		bookFileWriter.append(book2);
		bookFileWriter.append(book3);
		bookFileWriter.close();
 
		// deserializing
		DatumReader<Book> bookDatumReader = new SpecificDatumReader<Book>(
				Book.class);
		DataFileReader<Book> bookFileReader = new DataFileReader<Book>(store,
				bookDatumReader);
		while (bookFileReader.hasNext()) {
			Book b1 = bookFileReader.next();
			System.out.println("deserialized from file: " + b1);
		}
	}
 
}
Running the example code should produce the following output:

serializing books to temp file: /tmp/book5516033028097754203.avro
deserialized from file: {"name": "Programming is fun", "id": 123, "category": "Fiction"}
deserialized from file: {"name": "Some book", "id": 456, "category": "Horror"}
deserialized from file: {"name": "And another book", "id": 789, "category": null}

######################################################################################################

