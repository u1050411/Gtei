com.trueta.gtei
class Gtei : Application() {

        override fun onCreate() {
        super.onCreate()


        // Create the activity
        val activity = MainActivity()
        activity.viewModel.screens.value = screens
        startActivity(activity)
        }
        }