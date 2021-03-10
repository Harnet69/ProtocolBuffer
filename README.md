#Protocol buffers

Designed by Google for storing and interchanging all kinds of information
Protocol buffers are Google's language-neutral, platform-neutral, extensible mechanism for serializing structured data – think XML, but smaller, faster, and simpler
https://developer.android.com/codelabs/android-proto-datastore#0 / https://developers.google.com/protocol-buffers/docs/proto3
   Serialization - a process of translating a data structure or object state to a format in which it can be stored(file or memory dataBuffer) 
- use for serializing a data structure(similar to XML, but faster and simplier)
- decided once how the data should be structured and a compiler will generate code 

Proto data:
- install to Android studio Protocol Buffer Editor plugin
- create a folder with name "proto" in main directory(switch on project mode) define proto buf scheme in a filename.proto file
- instead creating a model class in kotlin we define it in protobuf scheme instead
```kotlin
  syntax = "proto3";

	//tels to a compiler where generate protocol buffer classes
	option java_package = "com.harnet.protocolbuffer";
	//create a separate file for each message object
	option java_multiple_files = true;

	//message object. Values is a fields unique number
	message Person{
  	   string firstName = 1;
  	   string lastName = 2;
  	   int32 age = 3;
	}
```
- on gradle build file(app) 
   - in plugins add: 
```kotlin
      id 'com.google.protobuf' version '0.8.12'
```
   - in dependencies add:
```kotlin
    //Proto DataStore
    implementation  "androidx.datastore:datastore-core:1.0.0-alpha04"
    implementation "androidx.datastore:datastore-preferences:1.0.0-alpha04"
    implementation  "com.google.protobuf:protobuf-javalite:3.10.0"
```
   - below dependencies add:
```kotlin
    protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.10.0"
    }

    // Generates the java Protobuf-lite code for the Protobufs in this project. See
    // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
    // for more information.
    generateProtoTasks {
        all().each { task ->
            task.builtins {
                java {
                    option 'lite'
                }
            }
        }
    }
```
- rebuild project to renerate files
- create a MySerializer class which extend Serializer and implement ita members
```kotlin
  // define how read and write an object to a data store
	class MySerializer: Serializer<Person> {
		//it will be used if there is no file created yet
    		override val defaultValue: Person
    		get() = Person.getDefaultInstance()

    		override fun readFrom(input: InputStream): Person {
        	try {
            	   return Person.parseFrom(input)
        	}catch (e: InvalidProtocolBufferException){
            	   throw CorruptionException("Cannot read .proto", e)
        	}
      		}

    		override fun writeTo(t: Person, output: OutputStream) {
        	t.writeTo(output)
    		}
	}
```
- create a ProtoRepository class
```kotlin
	class ProtoRepository(context: Context) {
    private val dataStore: DataStore<Person> = context.createDataStore(
            "my_data",
            serializer = MySerializer()
    )

    //initialize a dataStore(Choose Coroutines flow!!!)
    // use data store object to read a data
    val readProto: Flow<Person> = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.d("Error", exception.message.toString())
                    emit(Person.getDefaultInstance())
                } else {
                    throw exception
                }
            }

    //update Person's firstName
    suspend fun updatePersonFirstName(firstName: String) {
        dataStore.updateData { preference ->
            preference.toBuilder().setFirstName(firstName).build()
        }
    }
```
- create ViewModel which extends AndroidViewModel
```kotlin
 class MainViewModel(application: Application) : AndroidViewModel(application) {
    //create repository
    private val repository = ProtoRepository(application)

    val mFirstName = repository.readProto.asLiveData()

    fun updateFirstName(newFirstName: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.updatePersonFirstName(newFirstName)
    }

 }
```
- add to Fragment
```kotlin
  private fun observeViewModel() {
    viewModel.mFirstName.observe(viewLifecycleOwner, { person ->
            view?.findViewById<TextView>(R.id.saved_text)?.text = person.firstName
        })
    }

    private fun saveChanges() {
        view?.findViewById<Button>(R.id.btn_save)?.setOnClickListener {
            val firstName = view?.findViewById<EditText>(R.id.user_input)?.text
            if (firstName != null) {
                if (firstName.isNotEmpty()) {
                    viewModel.updateFirstName(firstName.toString())
                }
            }
        }
    }
```
