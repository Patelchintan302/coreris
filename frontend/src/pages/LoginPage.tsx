import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { getErrorMessage } from '../api/client';
import {
  Activity,
  Lock,
  User,
  ArrowRight,
  Shield,
  Stethoscope,
  Scan,
  UserCheck,
} from 'lucide-react';

export const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const { login } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const location = useLocation();

  const from = (location.state as { from?: { pathname: string } })?.from?.pathname || '/dashboard';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password.trim()) {
      toast.error('Please enter both username and password');
      return;
    }

    setSubmitting(true);
    try {
      await login({ username, password });
      toast.success(`Welcome back, ${username}!`);
      navigate(from, { replace: true });
    } catch (err: unknown) {
      toast.error(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const fillQuickAccount = (u: string, p: string) => {
    setUsername(u);
    setPassword(p);
  };

  return (
    <div className="min-h-screen bg-slate-950 flex">
      {/* Left side: Branding & Overview */}
      <div className="hidden lg:flex lg:w-1/2 relative bg-slate-900 border-r border-slate-800 p-12 flex-col justify-between overflow-hidden">
        {/* Ambient Glow */}
        <div className="absolute -top-32 -left-32 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute -bottom-32 -right-32 w-96 h-96 bg-sky-600/10 rounded-full blur-3xl pointer-events-none" />

        {/* Brand */}
        <div className="flex items-center gap-3 relative z-10">
          <div className="p-2.5 bg-gradient-to-tr from-blue-600 to-sky-500 rounded-xl shadow-lg shadow-blue-500/20 text-white">
            <Activity className="w-6 h-6" />
          </div>
          <span className="text-2xl font-black tracking-tight text-white">
            Core<span className="text-sky-400">RIS</span>
          </span>
        </div>

        {/* Center Hero Description */}
        <div className="relative z-10 space-y-6 max-w-lg">
          <h1 className="text-4xl font-extrabold text-white tracking-tight leading-tight">
            Next-Gen Radiology Information System
          </h1>
          <p className="text-slate-400 leading-relaxed text-base">
            Seamlessly coordinate patient intake, high-resolution imaging worklists, and automated OpenPDF diagnostic reporting with full role-based security.
          </p>

          <div className="grid grid-cols-2 gap-4 pt-4">
            <div className="p-4 rounded-xl bg-slate-800/40 border border-slate-700/50">
              <div className="text-2xl font-bold text-sky-400">100%</div>
              <div className="text-xs text-slate-400 mt-1">Audit Trail & Compliance</div>
            </div>
            <div className="p-4 rounded-xl bg-slate-800/40 border border-slate-700/50">
              <div className="text-2xl font-bold text-emerald-400">Automated</div>
              <div className="text-xs text-slate-400 mt-1">PDF Diagnostic Reports</div>
            </div>
          </div>
        </div>

        {/* Footer info */}
        <div className="text-xs text-slate-500 relative z-10">
          © 2026 CoreRIS Healthcare Informatics Platform. All rights reserved.
        </div>
      </div>

      {/* Right side: Login Form */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-6 sm:p-12">
        <div className="w-full max-w-md space-y-8">
          <div className="space-y-2 text-center lg:text-left">
            <h2 className="text-3xl font-bold text-white tracking-tight">Staff Portal Sign In</h2>
            <p className="text-sm text-slate-400">
              Enter your clinical credentials to access your workspace.
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Username Input */}
            <div className="space-y-1.5">
              <label className="text-xs font-semibold text-slate-300">Username</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-500">
                  <User className="w-4 h-4" />
                </div>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="e.g. user2"
                  className="w-full pl-10 pr-4 py-2.5 bg-slate-900 border border-slate-700/80 rounded-xl text-slate-100 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                  required
                />
              </div>
            </div>

            {/* Password Input */}
            <div className="space-y-1.5">
              <label className="text-xs font-semibold text-slate-300">Password</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-500">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full pl-10 pr-4 py-2.5 bg-slate-900 border border-slate-700/80 rounded-xl text-slate-100 placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                  required
                />
              </div>
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={submitting}
              className="w-full py-3 px-4 bg-gradient-to-r from-blue-600 to-sky-600 hover:from-blue-500 hover:to-sky-500 text-white font-medium rounded-xl text-sm shadow-lg shadow-blue-500/20 transition-all flex items-center justify-center gap-2 disabled:opacity-50"
            >
              {submitting ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  <span>Signing In...</span>
                </>
              ) : (
                <>
                  <span>Sign In</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </form>

          {/* Quick Demo Test Accounts */}
          <div className="pt-6 border-t border-slate-800 space-y-3">
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider text-center">
              Quick Test Roles (Click to Autofill)
            </p>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                onClick={() => fillQuickAccount('user2', 'password2')}
                className="flex items-center gap-2 p-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-800 text-left transition-colors text-xs"
              >
                <Stethoscope className="w-4 h-4 text-emerald-400 flex-shrink-0" />
                <div>
                  <div className="font-semibold text-slate-200">Radiologist</div>
                  <div className="text-[10px] text-slate-500">user2 / password2</div>
                </div>
              </button>

              <button
                type="button"
                onClick={() => fillQuickAccount('user1', 'password1')}
                className="flex items-center gap-2 p-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-800 text-left transition-colors text-xs"
              >
                <Scan className="w-4 h-4 text-sky-400 flex-shrink-0" />
                <div>
                  <div className="font-semibold text-slate-200">Technician</div>
                  <div className="text-[10px] text-slate-500">user1 / password1</div>
                </div>
              </button>

              <button
                type="button"
                onClick={() => fillQuickAccount('user3', 'password3')}
                className="flex items-center gap-2 p-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-800 text-left transition-colors text-xs"
              >
                <UserCheck className="w-4 h-4 text-amber-400 flex-shrink-0" />
                <div>
                  <div className="font-semibold text-slate-200">Receptionist</div>
                  <div className="text-[10px] text-slate-500">user3 / password3</div>
                </div>
              </button>

              <button
                type="button"
                onClick={() => fillQuickAccount('user0', 'password0')}
                className="flex items-center gap-2 p-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-800 text-left transition-colors text-xs"
              >
                <Shield className="w-4 h-4 text-purple-400 flex-shrink-0" />
                <div>
                  <div className="font-semibold text-slate-200">Administrator</div>
                  <div className="text-[10px] text-slate-500">user0 / password0</div>
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
