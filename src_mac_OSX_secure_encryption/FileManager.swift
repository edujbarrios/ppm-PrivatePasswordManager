import Foundation

class FileManager {
    func fileExists(atPath path: String) -> Bool {
        return FileManager.default.fileExists(atPath: path)
    }

    func deleteFile(atPath path: String) throws {
        try FileManager.default.removeItem(atPath: path)
    }
}
