import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { Activity, LogOut, User, Menu, Shield } from 'lucide-react';

interface NavbarProps {
  onToggleSidebar: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onToggleSidebar }) => {
  const { user, logout } = useAuth();

  const getRoleBadge = (role?: string) => {
    switch (role) {
      case 'ADMIN':
        return 'bg-purple-500/10 text-purple-400 border-purple-500/30';
      case 'RADIOLOGIST':
        return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      case 'TECHNICIAN':
        return 'bg-sky-500/10 text-sky-400 border-sky-500/30';
      case 'RECEPTIONIST':
        return 'bg-amber-500/10 text-amber-400 border-amber-500/30';
      default:
        return 'bg-slate-700 text-slate-300 border-slate-600';
    }
  };

  return (
    <header className="sticky top-0 z-30 flex items-center justify-between h-16 px-4 md:px-8 bg-slate-900/80 backdrop-blur-md border-b border-slate-800">
      {/* Left: Mobile Toggle & Brand */}
      <div className="flex items-center gap-3">
        <button
          onClick={onToggleSidebar}
          className="p-2 text-slate-400 hover:text-white rounded-xl hover:bg-slate-800 lg:hidden transition-colors"
          aria-label="Toggle menu"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div className="flex items-center gap-2.5">
          <div className="p-2 bg-gradient-to-tr from-blue-600 to-sky-500 rounded-xl shadow-lg shadow-blue-500/20 text-white">
            <Activity className="w-5 h-5" />
          </div>
          <div>
            <span className="text-lg font-bold tracking-tight text-white">Core<span className="text-sky-400">RIS</span></span>
            <span className="hidden sm:inline-block ml-2 text-xs font-semibold px-2 py-0.5 rounded-full bg-slate-800 text-slate-400 border border-slate-700">
              v1.0
            </span>
          </div>
        </div>
      </div>

      {/* Right: User Information & Logout */}
      <div className="flex items-center gap-3">
        {user && (
          <div className="flex items-center gap-3 bg-slate-800/60 border border-slate-700/60 px-3 py-1.5 rounded-xl">
            <div className="w-7 h-7 rounded-lg bg-blue-600/20 text-blue-400 flex items-center justify-center font-bold text-xs uppercase">
              {user.username.substring(0, 2)}
            </div>
            <div className="hidden sm:block text-left">
              <div className="text-xs font-medium text-slate-200">{user.username}</div>
            </div>
            <span
              className={`text-[10px] font-bold tracking-wider uppercase px-2 py-0.5 rounded-md border ${getRoleBadge(
                user.role
              )}`}
            >
              {user.role}
            </span>
          </div>
        )}

        <button
          onClick={logout}
          title="Sign out"
          className="flex items-center gap-1.5 px-3 py-2 text-xs font-medium text-slate-300 hover:text-rose-300 hover:bg-rose-950/40 rounded-xl transition-all border border-slate-800 hover:border-rose-900/50"
        >
          <LogOut className="w-4 h-4" />
          <span className="hidden md:inline">Sign Out</span>
        </button>
      </div>
    </header>
  );
};
