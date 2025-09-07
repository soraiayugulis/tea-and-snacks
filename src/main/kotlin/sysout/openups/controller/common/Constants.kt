package sysout.openups.controller.common

/**
 * Centralized API messages and descriptions
 */
object Constants {
    object Http {
        object Status {
            const val OK = "200"
            const val CREATED = "201"
            const val NO_CONTENT = "204"
            const val BAD_REQUEST = "400"
            const val NOT_FOUND = "404"
        }
    }
    object Message {
        object Error {
            object Entity {
                const val SAUCE_NOT_FOUND = "Sauce not found"
                const val SAUCE_INVALID_DATA = "Invalid sauce data provided"
                const val SAUCE_SNACK_NOT_FOUND = "Sauce or Snack not found"
                const val TEA_NOT_FOUND = "Tea not found"
                const val TEA_INVALID_DATA = "Invalid tea data provided"
                const val TEAS_NOT_FOUND = "No teas found matching the criteria"
                const val SNACK_NOT_FOUND = "Snack not found"
                const val SNACK_INVALID_DATA = "Invalid snack data provided"
            }
        }
        object Success {
            object Entity {
                const val SAUCE_FOUND = "Sauce found"
                const val SAUCE_CREATED = "Sauce created successfully"
                const val SAUCE_UPDATED = "Sauce updated successfully"
                const val SAUCE_DELETED = "Sauce deleted successfully"
                const val TEA_FOUND = "Tea found"
                const val TEA_CREATED = "Tea created successfully"
                const val TEA_UPDATED = "Tea updated successfully"
                const val TEA_DELETED = "Tea deleted successfully"
                const val TEAS_DELETED = "Teas deleted successfully, returns number of deleted items"
                const val SNACK_FOUND = "Snack found"
                const val SNACK_CREATED = "Snack created successfully"
                const val SNACK_UPDATED = "Snack updated successfully"
                const val SNACK_DELETED = "Snack deleted successfully"
                const val SAUCE_ADDED_TO_SNACK = "Sauce added to snack successfully"
                const val SAUCE_REMOVED_FROM_SNACK = "Sauce removed from snack successfully"
            }
        }
    }
    object List {
        const val SAUCE_FILTERED = "List of filtered sauces"
        const val SAUCE_BY_SNACK = "List sauces for a snack"
        const val TEA_FILTERED = "List of filtered teas"
        const val SNACK_FILTERED = "List of filtered snacks"
    }
    object Operation {
        const val SAUCE_UPDATE = "Update a sauce"
        const val SAUCE_FIND_BY_ID =  "Find sauce by ID"
        const val SAUCE_ADD = "Add a new sauce"
        const val SAUCE_DELETE = "Delete a sauce"
        const val SAUCE_DELETE_ALL = "Delete all sauces"
        const val SAUCE_TO_SNACK = "Add sauce to a snack"
        const val SAUCE_REMOVE_FROM_SNACK = "Remove sauce from a snack"
        const val TEA_UPDATE = "Update a tea"
        const val TEA_FIND_BY_ID =  "Find tea by ID"
        const val TEA_ADD = "Add a new tea"
        const val TEA_DELETE = "Delete a tea"
        const val TEA_DELETE_FILTER = "Delete teas with filtering"
        const val SNACK_UPDATE = "Update a snack"
        const val SNACK_FIND_BY_ID =  "Find snack by ID"
        const val SNACK_ADD = "Add a new snack"
        const val SNACK_DELETE = "Delete a snack"
        const val SNACK_DELETE_ALL = "Delete all snacks"
    }
}
