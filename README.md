# AndroidStudioProjects

A collection of Android Studio applications and course projects by @odyshbayeh.

## Project Overview

This repository contains multiple Android projects developed using Android Studio. The projects showcase different aspects of Android development, including database management, REST API integration, and UI features. Major sub-projects include:

- **FinalProject**: A Task Manager app allowing users to manage tasks with features such as user registration, login, task creation, search, reminders, and completion tracking. It uses SQLite for local data storage and can fetch data from a remote API.
- **RESTApplication**: Demonstrates connecting to a REST API, fetching JSON data, and displaying it within the app (e.g., a student list).

## Features

### FinalProject (Task Manager)
- User registration, login, and profile updates
- Manage tasks: add, update, delete, and search tasks by keyword and date range
- Task priorities and due dates
- Mark tasks as completed
- Task reminders
- Data stored locally using SQLite
- Remote fetch of tasks from a mock API

### RESTApplication
- Asynchronous REST API requests
- Parse and display JSON data
- Simple UI integration for data display

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/odyshbayeh/AndroidStudioProjects.git
   ```

2. **Open in Android Studio:**
   - Open Android Studio and select 'Open an existing project'.
   - Browse to the cloned repository folder.

3. **Set up dependencies:**
   - The project uses standard Android SDK and libraries; Android Studio will prompt to install any missing components.

4. **Run the projects:**
   - Select the desired app module (e.g., `FinalProject`, `RESTApplication`) from the run configuration.
   - Click 'Run' to build and launch on an emulator or device.

## Repository Structure

- `/FinalProject` - Task manager app source code
- `/RESTApplication` - REST API sample app source code
- Other folders may contain additional course-related Android projects

## Requirements

- Android Studio (latest recommended)
- Android SDK (API level as specified in each module)
- Internet connection (for REST API functionality)

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for suggestions or improvements.

## License

This project currently does not have a license file. Please add one if you intend to share or distribute your work.
