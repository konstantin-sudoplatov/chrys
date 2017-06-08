module concept.routes;
public import tools;

/// Concept ID type.
alias CID = int;

/// A route to a concept.
alias Route = void*;

/// Concept directory type: associative array of a concept route by concept IDs.
alias CDIR = Route[CID];

/// Static concept IDs
enum SCpt: ushort {
    listen
}

/// User ID type.
alias UID = uint;

/// Common concept directory.
shared CDIR comCDir;

/// Private concept directories: associative array of a private concept directory by user ID.
CDIR[UID] privCDir;

